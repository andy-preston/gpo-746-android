package gpo_746

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class UsbSystemProduction(m: UsbManager, d: UsbDevice) : UsbSystemInterface {

    private val usbManager = m
    private val device = d

    private var usbTimeout: Int = 1000
    private var connection: UsbDeviceConnection? = null
    private var bulkReadEndpoint: UsbEndpoint? = null
    private var packetSize: Int = 0

    private fun findBulkReadEndpoint(usbInterface: UsbInterface): UsbEndpoint? {
        for (endpointNumber in 0..usbInterface.getEndpointCount() - 1 ) {
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

    ////////////////////////////////////////////////////////////////////////////

    override public fun open(vid: UShort, pid: UShort, timeout: Int) {
        usbTimeout = timeout
        connection = usbManager.openDevice(device)
        val usbInterface = device.getInterface(0)
        val connected = connection?.let { it.claimInterface(usbInterface, true)}
        if (connected == null || !connected) {
            throw Exception("Could not claim interface")
        }
        bulkReadEndpoint = findBulkReadEndpoint(usbInterface)
        val size = bulkReadEndpoint?.let { it.getMaxPacketSize() }
        packetSize = if (size != null) size else 0
    }

    override public fun close() {
        connection?.let {
            if (!it.releaseInterface(device.getInterface(0))) {
                throw Exception("Could not release interfaces")
            }
            it.close()
        }
        connection = null
    }

    override public fun bulkRead(): ByteArray {
        val buffer = ByteArray(packetSize + 1)
        val bytesRead: Int? = connection?.let {
            it.bulkTransfer(
                bulkReadEndpoint,
                buffer,
                packetSize,
                usbTimeout
            )
        }
        if (bytesRead == null) {
            throw Exception("Attempted bulk transfer with null connection");
        } else if (bytesRead < 0) {
            /* Regarding the return value of bulk transfer, the docs say:
            "length of data transferred (or zero) for success,
                    or negative value for failure"
            Yeah, thanks for that - "a negative value" is very helpful!
            Watch out that timeout doesn't return an error code like libusb does.
            */
            throw Exception("Bulk transfer failed? ${bytesRead}")
        } else {
            buffer[bytesRead] = 0
        }
        return buffer
    }

    override public fun read(
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
                usbTimeout
            )
        }
        if (bytesRead != 2) {
            throw Exception("Read Registers did not return 2 bytes ${bytesRead}")
        }
        return buffer
    }

    override public fun write(
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
                usbTimeout
            )
        }
        if (result != null && result < 0) {
            throw Exception("Write Registers failed ${result}");
        }
    }
}
