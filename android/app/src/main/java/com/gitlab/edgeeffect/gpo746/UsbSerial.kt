package com.gitlab.edgeeffect.gpo746

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbConstants

sealed class IntegerResult {
    data class Success(val value: Int): IntegerResult()
    data class Error(val message: String): IntegerResult()
}

sealed class BufferResult {
    data class Success(val value: ByteArray): BufferResult()
    data class Error(val message: String): BufferResult()
}

// Some of these MAY be specific to the CH340G???
// If so, there's some hideous type-juggling to do later!
enum class Request(val code: Int) {
    VENDOR_VERSION(0x5F),
    VENDOR_READ(0x95),
    VENDOR_WRITE(0x9A),
    VENDOR_SERIAL_INIT(0xA1),
    VENDOR_MODEM_OUT(0xA4)
}

class UsbSerialBase(d: UsbDevice, c: UsbDeviceConnection) {

    private val usbDevice = d
    private val usbDeviceConnection = c

    protected lateinit var readEndpoint: UsbEndpoint
    protected lateinit var writeEndpoint: UsbEndpoint

    private val USB_TIMEOUT_MILLISECONDS = 5000

    private fun claimInterface(): IntegerResult {
        for (interfaceNum in 0..usbDevice.getInterfaceCount() - 1) {
            val usbInterface: UsbInterface = usbDevice.getInterface(interfaceNum)
            if (!usbDeviceConnection.claimInterface(usbInterface, true)) {
                return IntegerResult.Error("could not claim interfaces")
            }
        }
        return IntegerResult.Success(0)
    }

    private fun setupEndpoints(): IntegerResult {
        val dataInterface: UsbInterface = usbDevice.getInterface(
            usbDevice.getInterfaceCount() - 1
        )
        for (endpointNum in 0..dataInterface.getEndpointCount() - 1 ) {
            val endpoint: UsbEndpoint = dataInterface.getEndpoint(epNum)
            if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                    readEndpoint = endpoint
                } else {
                    writeEndpoint = endpoint
                }
            }
        }
        // Really? You think there are no errors to check here?
        return IntegerResult.Success(0)
    }

    public fun openInterfaces(): IntegerResult {
        val claimed = claimInterface()
        return when (claimed) {
            is IntegerResult.Success -> {
                setupEndpoints()
            }
            is IntegerResult.Error -> {
                claimed
            }
        }
    }

    protected fun controlOut(
        request: Request,
        value: Int,
        index: Int
    ): IntegerResult {
        val result = usbDeviceConnection.controlTransfer(
            UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_OUT,
            request.code and 0xff,
            value and 0xff,
            index and 0xff,
            null,
            0,
            USB_TIMEOUT_MILLISECONDS
        )
        if (result < 0) {
            return IntegerResult.Error("controlOut error $result")
        }
        return IntegerResult(result)
    }

    protected fun controlIn(
        request: Request,
        value: Int,
        index: Int,
        size: Int
    ): BufferResult {
        var buffer = ByteArray(size)
        val result = usbDeviceConnection.controlTransfer(
            UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_IN,
            request.code and 0xff,
            value and 0xff,
            index and 0xff,
            buffer,
            buffer.size,
            USB_TIMEOUT_MILLISECONDS
        )
        if (result < 0) {
            return BufferResult.Error("controlIn error $result")
        }
        if (result != size) {
            return BufferResult.Error("controlIn expected: $size actual: $result")
        }
        return BufferResult.Success(buffer)
    }

    protected fun checkedControlIn(
        request: Request,
        value: Int,
        expected: IntArray
    ): IntegerResult {
        val size = expected.size
        val result = controlIn(request, value, 0, expected.size)
        when (result) {
            is BufferResult.Success -> {
                // Just carry on
            }
            is BufferResult.Error -> {
                return result
            }
        }
        for ((index, item) in expected.withIndex()) {
            val otherItem = result.buffer[index]
            // expected can contain values of -1 that shouldn't be compared
            if (item > -1 && item != otherItem.toInt()) {
                val exp = item.toString(16)
                val got = otherItem.toString(16)
                return IntegerResult.Error("expected $exp but got $got")
            }
        }
        return IntegerResult.Success(0)
    }

}
