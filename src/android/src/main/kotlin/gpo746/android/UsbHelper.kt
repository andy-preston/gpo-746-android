package andyp.gpo746.android

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import andyp.gpo746.UsbSystemInterface

private const val TIMEOUT_MILLISECONDS = 1000

class UsbHelper : UsbSystemInterface {

    private var usbDevice: UsbDevice? = null
    private var usbDeviceConnection: UsbDeviceConnection? = null
    private var usbInterface: UsbInterface? = null
    private var bulkReadEndpoint: UsbEndpoint? = null
    private var packetSize: Int = 0

    public fun openDevice(device: UsbDevice?, usbManager: UsbManager) {
        Log.i("gpo746", "UsbHelper - OpenDevice")
        if (device == null) {
            exception("Device is null")
        }
        usbDevice = device
        usbDeviceConnection = usbManager.openDevice(usbDevice)
        usbInterface = usbDevice?.getInterface(0)
        val claimed = usbDeviceConnection?.let {
            it.claimInterface(usbInterface, true)
        }
        if (claimed == null || !claimed) {
            Log.e("gpo746", "Could not claim interface ($claimed)")
        }
        findBulkReadEndpoint()
    }

    private fun findBulkReadEndpoint(): UsbEndpoint? {
        bulkReadEndpoint = null
        usbInterface?.let {
            for (endpointNumber in 0..it.getEndpointCount() - 1) {
                val endpoint = it.getEndpoint(endpointNumber)
                if (
                    endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                    endpoint.getDirection() == UsbConstants.USB_DIR_IN
                ) {
                    bulkReadEndpoint = endpoint
                }
            }
        }
        return null
    }

    private fun exception(message: String) {
        Log.e("gpo746", "UsbHelper - $message")
        throw Exception(message)
    }

    public fun closeDevice() {
        usbDeviceConnection?.let {
            Log.i("gpo746", "UsbSystem - Release interface")
            val result = it.releaseInterface(usbInterface)
            if (!result) {
                Log.e("gpo746", "UsbSystem - Could not release interface ($result)")
            }
            Log.i("gpo746", "UsbSystem - Close connection")
            it.close()
        }
        usbDeviceConnection = null
    }

    /**************************************************************************/

    public override fun bulkRead(): ByteArray {
        val buffer = ByteArray(packetSize + 1)
        val bytesRead: Int? = usbDeviceConnection?.let {
            it.bulkTransfer(
                bulkReadEndpoint,
                buffer,
                packetSize,
                TIMEOUT_MILLISECONDS
            )
        }
        if (bytesRead == null) {
            exception("Attempted bulk transfer with null connection")
        } else if (bytesRead < 0) {
            /* Regarding the return value of bulk transfer, the docs say:
             * "length of data transferred (or zero) for success,
             *      or negative value for failure"
             * Yeah, thanks for that - "a negative value" is very helpful!
             * I'm certainly getting -1 returned for timeouts, whether I get -1
             * for other errors or specific codes, I still don't know.
             * (Just for reference, in libusb TIMEOUT is -7 - but there's no
             * reason for parity in error codes.)
             */
            if (bytesRead != -1) {
                exception("Bulk read returned error code $bytesRead")
            }
            buffer[0] = 0
        } else {
            buffer[bytesRead] = 0
        }
        return buffer
    }

    public override fun read(
        requestCode: UByte,
        addressOrPadding: UShort
    ): ByteArray? {
        val buffer = ByteArray(2)
        val bytesRead: Int? = usbDeviceConnection?.let {
            it.controlTransfer(
                UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_IN,
                requestCode.toInt(),
                addressOrPadding.toInt(),
                0,
                buffer,
                2,
                TIMEOUT_MILLISECONDS
            )
        }
        if (bytesRead == 2) {
            return buffer
        } else {
            Log.e("gpo746", "Read Registers did not return 2 bytes $bytesRead")
            return null
        }
    }

    public override fun write(
        requestCode: UByte,
        addressOrValue: UShort,
        valueOrPadding: UShort
    ) {
        val result: Int? = usbDeviceConnection?.let {
            it.controlTransfer(
                UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_OUT,
                requestCode.toInt(),
                addressOrValue.toInt(),
                valueOrPadding.toInt(),
                null,
                0,
                TIMEOUT_MILLISECONDS
            )
        }
        if (result != null && result < 0) {
            exception("Write Registers failed $result")
        }
    }
}
