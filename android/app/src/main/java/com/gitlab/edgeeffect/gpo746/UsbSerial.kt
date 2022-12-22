package com.gitlab.edgeeffect.gpo746

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface

sealed class IntegerResult {
    data class Success(val value: Int): IntegerResult()
    data class Error(val message: String): IntegerResult()
}

sealed class BufferResult {
    data class Success(val buffer: ByteArray): BufferResult()
    data class Error(val message: String): BufferResult()
}

sealed class StringResult {
    data class Success(val value: String): StringResult()
    data class Error(val message: String): StringResult()
}

enum class Request(val code: Int) {
    VENDOR_VERSION(0x5F),
    VENDOR_READ(0x95),
    VENDOR_WRITE(0x9A),
    VENDOR_SERIAL_INIT(0xA1),
    VENDOR_MODEM_OUT(0xA4)
}

open class UsbSerial(d: UsbDevice, c: UsbDeviceConnection) {

    private val usbDevice = d
    private val usbDeviceConnection = c

    protected lateinit var receiveEndpoint: UsbEndpoint
    protected lateinit var sendEndpoint: UsbEndpoint

    private val timeoutMilliseconds = 5000

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
        for (endpointNumber in 0..dataInterface.getEndpointCount() - 1 ) {
            val endpoint: UsbEndpoint = dataInterface.getEndpoint(endpointNumber)
            if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                    receiveEndpoint = endpoint
                } else {
                    sendEndpoint = endpoint
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
            timeoutMilliseconds
        )
        if (result < 0) {
            return IntegerResult.Error("controlOut error $result")
        }
        return IntegerResult.Success(result)
    }

    protected fun controlIn(
        request: Request,
        value: Int,
        length: Int
    ): BufferResult {
        var buffer = ByteArray(length)
        val result = usbDeviceConnection.controlTransfer(
            UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_IN,
            request.code and 0xff,
            value and 0xff,
            0,
            buffer,
            buffer.size,
            timeoutMilliseconds
        )
        if (result < 0) {
            return BufferResult.Error("controlIn error $result")
        }
        if (result != length) {
            return BufferResult.Error("controlIn expected: $length actual: $result")
        }
        return BufferResult.Success(buffer)
    }

    public fun send(string: String): IntegerResult {
        val buffer = string.toByteArray(Charsets.UTF_8)
        val length = buffer.size
        val result = usbDeviceConnection.bulkTransfer(
            sendEndpoint,
            buffer,
            length,
            timeoutMilliseconds
        )
        return if (result < 0) {
            IntegerResult.Error("Failed to send $result")
        } else {
            IntegerResult.Success(result)
        }
    }

    public fun receive(length: Int): StringResult {
        var buffer = ByteArray(length)
        val result = usbDeviceConnection.bulkTransfer(
            receiveEndpoint,
            buffer,
            length,
            timeoutMilliseconds
        )
        return if (result < 0) {
            StringResult.Error("Failed to receive $result")
        } else if (result == 0) {
            StringResult.Success("")
        } else {
            StringResult.Success(String(buffer))
        }
    }

}
