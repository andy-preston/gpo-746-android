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
        throw Exception("Not implemented")
    }

    override public fun read(
        requestCode: UByte,
        addressOrPadding: UShort
    ): ByteArray {
        val buffer = ByteArray(2)
        val ret: Int? = connection?.let {
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
        if (ret != 2) {
            throw Exception("USB Read Registers did not return 2 bytes")
        }
        return buffer
    }

    override public fun write(
        requestCode: UByte,
        addressOrValue: UShort,
        valueOrPadding: UShort
    ) {
        throw Exception("Not implemented")
    }
}
