package com.gitlab.edgeeffect.gpo746

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface

/*
CH340G driver

I keep telling my boss that rolling your own when there's a perfectly
good library available is just plain stoopid! And, here I am, doing just
that thing! But I wanted something nicely difficult to do to help me
learn Kotlin and here I am.

I'm basing this on the Linux device driver for ch34x chips at:
https://github.com/lizard43/CH340G/blob/master/ch340g/ch34x.c
and two Android Java libraries at
https://github.com/felHR85/UsbSerial/blob/7fff8b6d5ca19590dcb05c3f977970e8cce103b7/usbserial/src/main/java/com/felhr/usbserial/CH34xSerialDevice.java
and
https://github.com/mik3y/usb-serial-for-android/blob/master/usbSerialForAndroid/src/main/java/com/hoho/android/usbserial/driver/Ch34xSerialDriver.java

The previous implementations aren't particularly self-documented, which I'm
trying to improve on here. Although I've still got a few "horribles" where I
don't yet understand what my source materials are doing.

If I can find more correct information, I'll update the naming here to match.

I'm also using the strong typing offered by Kotlin to "Make Invalid States
Unrepresentable"

This is a work in progress - expect holes, inconsistencies and mistakes!
*/

sealed class MyResult {
    data class Success(val value: Int): MyResult()
    data class Error(val message: String): MyResult()
}

class UsbSerial(usbDevice: UsbDevice, usbDeviceConnection: UsbDeviceConnection) {

    private val device = usbDevice
    private val connection = usbDeviceConnection

    private lateinit var readEndpoint: UsbEndpoint
    private lateinit var writeEndpoint: UsbEndpoint

    private enum class Request(val code: Int) {
        VENDOR_VERSION(0x5F),
        VENDOR_READ(0x95),
        VENDOR_WRITE(0x9A),
        VENDOR_SERIAL_INIT(0xA1),
        VENDOR_MODEM_OUT(0xA4)
    }

    private enum class Register(val address: Int) {
        BAUDRATE_1(0x1312),
        LCR(0x2518),
        STATUS(0x0706),
        BAUDRATE_2(0x0F2C)
    }

    private val USB_TIMEOUT_MILLIS = 5000

    public fun openInterfaces(): MyResult {
        for (intNum in 0..device.getInterfaceCount() - 1) {
            val usbInterface: UsbInterface = device.getInterface(intNum)
            if (!connection.claimInterface(usbInterface, true)) {
                return MyResult.Error("could not claim interfaces")
            }
        }
        val dataInterface: UsbInterface = device.getInterface(
            device.getInterfaceCount() - 1
        )
        for (epNum in 0..dataInterface.getEndpointCount() - 1 ) {
            val endpoint: UsbEndpoint = dataInterface.getEndpoint(epNum)
            if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                    readEndpoint = endpoint
                } else {
                    writeEndpoint = endpoint
                }
            }
        }
        return MyResult.Success(0)
    }

    /* This is basically just a wrapper around connection.controlTransfer */
    private fun controlOut(request: Request, value: Int, index: Int): Int {
        return connection.controlTransfer(
            UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_OUT,
            request.code,
            value,
            index,
            null,
            0,
            USB_TIMEOUT_MILLIS
        )
    }

    /* This is basically just a wrapper around connection.controlTransfer */
    private fun controlIn(request: Request, value: Int, index: Int, buffer: ByteArray): Int {
        return connection.controlTransfer(
            UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_IN,
            request.code,
            value,
            index,
            buffer,
            buffer.size,
            USB_TIMEOUT_MILLIS
        )
    }

    /* in the original code this is called checkState */
    private fun checkedControlIn(request: Request, value: Int, expected: IntArray): MyResult {
        val size = expected.size
        val buffer = ByteArray(size)
        val ret: Int = controlIn(request, value, 0, buffer)
        if (ret < 0) {
            return MyResult.Error("request")
        }
        if (ret != size) {
            return MyResult.Error("expected $size bytes but got $ret")
        }
        for ((index, item) in expected.withIndex()) {
            val otherItem = buffer[index]
            // expected can contain values of -1 that shouldn't be compared
            if (item > -1 && item != otherItem.toInt()) {
                val exp = item.toString(16)
                val got = otherItem.toString(16)
                return MyResult.Error("expected $exp but got $got")
            }
        }
        return MyResult.Success(0)
    }

    private fun vendorWrite(register: Register, value: Int): Int {
        return controlOut(
            Request.VENDOR_WRITE,
            register.address,
            value
        )
    }

    private fun vendorExpect(register: Register, expected: IntArray): MyResult {
        return checkedControlIn(
            Request.VENDOR_READ,
            register.address,
            expected
        )
    }

    private fun defaultBaudRate(): MyResult {
        val BAUDBASE_FACTOR: Long = 1532620800
        val BAUDBASE_DIVMAX: Long = 3
        var factor: Long = BAUDBASE_FACTOR / 9600
        var divisor: Long = BAUDBASE_DIVMAX
        while ((factor > 0xfff0) && divisor > 0) {
            factor = factor shl 3
            divisor = divisor - 1
        }
        factor = 0x10000 - factor;
        divisor = divisor or 0x0080 // else ch341a waits until buffer full (????)
        val result1: Int = vendorWrite(
            Register.BAUDRATE_1,
            ((factor and 0xff00) or divisor).toInt()
        )
        if (result1 < 0) {
            return MyResult.Error("Baud rate failed - Register 1")
        }
        val result2 = vendorWrite(
            Register.BAUDRATE_2,
            (factor and 0xff).toInt()
        );
        if (result2 < 0) {
            return MyResult.Error("Baud rate failed - Register 2")
        }
        return MyResult.Success(0)
    }

    private fun initialize(): MyResult {
        val vendorVersion = checkedControlIn(
            Request.VENDOR_VERSION,
            0,
            intArrayOf(-1, 0x00)
        )
        if (vendorVersion is MyResult.Error) {
            return MyResult.Error("Vendor version failed - " + vendorVersion)
        }
        if (controlOut(Request.VENDOR_SERIAL_INIT, 0, 0) < 0) {
            return MyResult.Error("Serial Init Failed")
        }
        val baudRate = defaultBaudRate()
        if (baudRate is MyResult.Error) {
            return baudRate
        }
        val lcrCheck = vendorExpect(
            Register.LCR,
            intArrayOf(-1, 0x00)
        )
        if (lcrCheck is MyResult.Error) {
            return MyResult.Error("Failed to check LCR 2518 - " + lcrCheck)
        }
        val lcrWrite = vendorWrite(
            Register.LCR,
            lineControlRegister(setOf(
                LcrBit.ENABLE_RX, LcrBit.ENABLE_TX, LcrBit.CS8
            ))
        )
        if (lcrWrite < 0) {
            return MyResult.Error("Failed to into LCR 2518")
        }
        val checkStatus = checkedControlIn(
            Request.vendorRead,
            Register.STATUS,
            intArrayOf(-1, -1)
        )
        if (checkStatus is MyResult.Error) {
            return MyResult.Error("Check status failed - " + something)
        }
        if (controlOut(Request.VENDOR_SERIAL_INIT, 0x501f, 0xd90a) < 0) {
            throw new IOException("Init failed: #7")
        }
        return MyResult.Success(0)
    }
}
