package com.gitlab.edgeeffect.gpo746

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection

enum class Register(val address: Int) {
    BAUD_RATE_1(0x1312),
    LCR(0x2518),
    GCL(0x0706),
    BAUD_RATE_2(0x0F2C)
}

enum class BaudRate(val controlWords: Pair<Int, Int>) {
    B2400(Pair(0xd901, 0x0038)),
    B4800(Pair(0x6402, 0x001f)),
    B9600(Pair(0xb202, 0x0013)),
    B19200(Pair(0xd902, 0x000d)),
    B38400(Pair(0x6403, 0x000a)),
    B115200(Pair(0xcc03, 0x0008))
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
    CS5(0x00);
    companion object {
        fun asByte(bits: Set<LcrBit>): Int {
            return bits.fold(0) {
                byte: Int, bit: LcrBit -> byte or bit.value
            }
        }
    }
}

enum class ModemBit(val value : Int) {
    DTR(0x20),
    RTS(0x40);
    companion object {
        fun asByte(bits: Set<ModemBit>): Int {
            return bits.fold(0) {
                byte: Int, bit: ModemBit -> byte or bit.value
            }
        }
    }
}

// My ARM/C Prototype used the high nybble but here and in other implementations
// we're using the low nybble???
enum class GclBit(val value: Int) {
    CTS(0x01),
    DSR(0x02),
    RI(0x04),
    DCD(0x08);
    companion object {
        fun fromByte(byte: Byte): Set<GclBit> {
            val given = byte.toInt()
            return (GclBit.values().filter {
                given and it.value == it.value
            }).toSet()
        }
    }
}

sealed class HandshakeResult {
    data class Success(val value: Set<GclBit>): HandshakeResult()
    data class Error(val message: String): HandshakeResult()
}

class CH340G(d: UsbDevice, c: UsbDeviceConnection) : UsbSerial (d, c) {
    public fun setHandshake(bits: Set<ModemBit>): IntegerResult {
        val result = controlOut(
            Request.VENDOR_MODEM_OUT,
            ModemBit.asByte(bits),
            0
        )
        return when (result) {
            is IntegerResult.Success -> {
                result
            }
            is IntegerResult.Error -> {
                IntegerResult.Error("Failed to set handshake $result.message")
            }
        }
    }

    public fun getHandshake(): HandshakeResult {
        // We get 2 bytes from the status register.
        // The second byte is always 0x1B and I'm still unsure of what it's for.
        val result = controlIn(
            Request.VENDOR_READ,
            Register.GCL.address,
            2
        )
        return when (result) {
            is BufferResult.Success -> {
                HandshakeResult.Success(GclBit.fromByte(result.buffer[0]))
            }
            is BufferResult.Error -> {
                HandshakeResult.Error("Failed to get handshake $result.message")
            }
        }
    }

    private fun setBaudRate(baudRate: BaudRate): IntegerResult {
        val (baud1, baud2) = baudRate.controlWords
        val result1 = controlOut(
            Request.VENDOR_WRITE,
            Register.BAUD_RATE_1.address,
            baud1
        )
        val result2 = when (result1) {
            is IntegerResult.Success -> {
                controlOut(
                    Request.VENDOR_WRITE,
                    Register.BAUD_RATE_2.address,
                    baud2
                )
            }
            is IntegerResult.Error -> {
                result1
            }
        }
        return when (result2) {
            is IntegerResult.Success -> {
                result2
            }
            is IntegerResult.Error -> {
                IntegerResult.Error("Failed to set baud rate $result2.message")
            }
        }
    }

    private fun setupLcr(): IntegerResult {
        val result = controlOut(
            Request.VENDOR_WRITE,
            Register.LCR.address,
            LcrBit.asByte(setOf(
                LcrBit.ENABLE_TX,
                LcrBit.ENABLE_RX,
                LcrBit.CS8
            ))
        )
        return when (result) {
            is IntegerResult.Success -> {
                result
            }
            is IntegerResult.Error -> {
                IntegerResult.Error("Failed to set LCR: $result.message")
            }
        }

    }

    private fun initialiseSerial(): IntegerResult {
        val result = controlOut(
            Request.VENDOR_SERIAL_INIT,
            0,
            0
        )
        return when (result) {
            is IntegerResult.Success -> {
                result
            }
            is IntegerResult.Error -> {
                IntegerResult.Error("Failed serial init: $result.message")
            }
        }
    }

    public fun start(): IntegerResult {
        val opened = openInterfaces()
        val initialised = when (opened) {
            is IntegerResult.Success -> {
                initialiseSerial()
            }
            is IntegerResult.Error -> {
                opened
            }
        }
        val lcrSetup = when(initialised) {
            is IntegerResult.Success -> {
                setupLcr()
            }
            is IntegerResult.Error -> {
                initialised
            }
        }
        val baudSetup = when(lcrSetup) {
            is IntegerResult.Success -> {
                setBaudRate(BaudRate.B9600)
            }
            is IntegerResult.Error -> {
                lcrSetup
            }
        }
        return when(baudSetup) {
            is IntegerResult.Success -> {
                setHandshake(setOf<ModemBit>())
            }
            is IntegerResult.Error -> {
                baudSetup
            }
        }
    }
}
