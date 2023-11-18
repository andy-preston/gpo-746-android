package andyp.gpo746

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.util.Log

private const val DEFAULT_TIMEOUT = 1000

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class UsbSystemProduction(d: UsbDevice, m: UsbManager) : UsbSystemInterface {

    private val usbManager = m
    private val device = d

    private var timeoutMilliseconds: Int = DEFAULT_TIMEOUT
    private var connection: UsbDeviceConnection? = null
    private var bulkReadEndpoint: UsbEndpoint? = null
    private var packetSize: Int = 0

    private fun findBulkReadEndpoint(usbInterface: UsbInterface): UsbEndpoint? {
        for (endpointNumber in 0..usbInterface.getEndpointCount() - 1) {
            val endpoint = usbInterface.getEndpoint(endpointNumber)
            if (
                endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                endpoint.getDirection() == UsbConstants.USB_DIR_IN
            ) {
                return endpoint
            }
        }
        return null
    }

    // // // // // // // // // // // // // // // // // // // // // // // // //

    private fun exception(message: String) {
        Log.e("gpo746", message)
        throw Exception(message)
    }

    public override fun start(vid: UShort, pid: UShort, timeout: Int) {
        timeoutMilliseconds = timeout
        connection = usbManager.openDevice(device)
        val usbInterface = device.getInterface(0)
        val connected = connection?.let {
            it.claimInterface(usbInterface, true)
        }
        if (connected == null || !connected) {
            exception("Could not claim interface")
        }
        bulkReadEndpoint = findBulkReadEndpoint(usbInterface)
        val size = bulkReadEndpoint?.let { it.getMaxPacketSize() }
        packetSize = if (size != null) size else 0
    }

    public override fun finish() {
        connection?.let {
            val result = it.releaseInterface(device.getInterface(0))
            if (!result) {
                Log.e("gpo746", "Could not release interface")
            }
            it.close()
        }
        connection = null
    }

    public override fun bulkRead(): ByteArray {
        val buffer = ByteArray(packetSize + 1)
        val bytesRead: Int? = connection?.let {
            it.bulkTransfer(
                bulkReadEndpoint,
                buffer,
                packetSize,
                timeoutMilliseconds
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
    ): ByteArray {
        val buffer = ByteArray(2)
        val bytesRead: Int? = connection?.let {
            it.controlTransfer(
                UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_IN,
                requestCode.toInt(),
                addressOrPadding.toInt(),
                0,
                buffer,
                2,
                timeoutMilliseconds
            )
        }
        if (bytesRead != 2) {
            exception("Read Registers did not return 2 bytes $bytesRead")
        }
        return buffer
    }

    public override fun write(
        requestCode: UByte,
        addressOrValue: UShort,
        valueOrPadding: UShort
    ) {
        val result: Int? = connection?.let {
            it.controlTransfer(
                UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_OUT,
                requestCode.toInt(),
                addressOrValue.toInt(),
                valueOrPadding.toInt(),
                null,
                0,
                timeoutMilliseconds
            )
        }
        if (result != null && result < 0) {
            exception("Write Registers failed $result")
        }
    }
}
