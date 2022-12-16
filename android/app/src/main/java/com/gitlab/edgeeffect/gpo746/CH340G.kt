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

I'm also trying to use the strong typing offered by Kotlin to "Make Invalid
States Unrepresentable"

This is a work in progress - expect holes, inconsistencies and mistakes!
*/
package com.gitlab.edgeeffect.gpo746

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection

enum class BaudRate(val controlWords: Pair<Int, Int>) {
    BAUD_2400(Pair(0xd901, 0x0038)),
    BAUD_4800(Pair(0x6402, 0x001f)),
    BAUD_9600(Pair(0xb202, 0x0013)),
    BAUD_19200(Pair(0xd902, 0x000d)),
    BAUD_38400(Pair(0x6403, 0x000a)),
    BAUD_115200(Pair(0xcc03, 0x0008))
}

enum class Register(val address: Int) {
    BAUD_RATE_1(0x1312),
    LCR(0x2518),
    GCL(0x0706),
    BAUD_RATE_2(0x0F2C)
}

enum class LcrBit(val value: Int) {
    ENABLE_RX(0x80),
    ENABLE_TX(0x40),
    MARK_SPACE(0x20),
    PAR_EVEN(0x10),
    ENABLE_PAR(0x08),
    STOP_BITS_2(0x04),
    CS8(0x03),
    CS7(0x02),
    CS6(0x01),
    CS5(0x00)
}

fun lineControlRegister(bits: Set<LcrBit>): Int {
    return bits.fold(0) { byte: Int, bit: LcrBit -> byte or bit.value }
}

enum class ModemBit(val value : Int) {
    DTR(0x20),
    RTS(0x40)
}

// The handshaking registers use inverted logic hence the xor 0xff
fun modemRegister(bits: Set<ModemBit>): Int {
    return bits.fold(0) { byte: Int, bit: ModemBit -> byte or bit.value } xor 0xff
}

// Other implementations seem to use the Low nybble, but the low nybble
// seems to work for me
enum class GclBit(val value: Int) {
    CTS(0x10),
    DSR(0x20),
    RI(0x40),
    DCD(0x80)
}

// The handshaking registers use inverted logic hence we check if a bit = 0
fun gclRegister(byte: Int): Set<GclBit> {
    return (GclBit.value.filter { byte and it.value == 0 }).toSet()
}

sealed class HandshakeResult {
    data class Success(val value: Set<GclBit>): HandshakeResult()
    data class Error(val message: String): HandshakeResult()
}

class CH340G(d: UsbDevice, c: UsbDeviceConnection) : UsbSerial (d, c) {

    private fun vendorWrite(register: Register, value: Int): Int {
        return controlOut(
            Request.VENDOR_WRITE,
            register.address,
            value
        )
    }

    private fun vendorExpect(register: Register, expected: IntArray): IntegerResult {
        return checkedControlIn(
            Request.VENDOR_READ,
            register.address,
            expected
        )
    }

    private fun vendorRead(register: Register, size: Integer): BufferResult {
        return controlIn(
            Request.VENDOR_READ,
            register.address,
            buffer,
            size
        )
    }

    private fun setHandshake(bits: Set<ModemBit>): IntegerResult {
        return controlOut(VENDOR_MODEM_OUT, modemRegister(bits), 0)
    }

    private fun getHandshake(): HandshakeResult {
        // We get 2 bytes from the status register
        // but I'm still unsure of what the second byte is for
        val result = vendorRead(Register.GCL, 2)
        return when (result) {
            is BufferResult.Success -> {
                HandshakeResult.Success(gclRegister(result.buffer[0]))
            }
            is BufferResult.Error -> {
                HandshakeResult.Error(result.message)
            }
        }
    }

    private function setBaudRate(baudRate: BaudRate) : IntegerResult {
        val (baud1, baud2) = baudRate.controlWords
        val result = vendorWrite(BAUD_RATE_1, baud1);
        return when (result) {
            is IntegerResult.Success -> {
                vendorWrite(BAUD_RATE_2, baud2);
            }
            is IntegerResult.Error -> {
                result
            }
        }
    }

    public function initialise(): IntegerResult {

    }

}
