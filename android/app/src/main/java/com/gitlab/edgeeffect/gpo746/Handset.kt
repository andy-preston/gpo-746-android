package com.gitlab.edgeeffect.gpo746

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection

sealed class BooleanResult {
    data class Success(val value: Boolean): BooleanResult()
    data class Error(val message: String): BooleanResult()
}

class Handset {
    private lateinit var usbSerial: CH340G
    private var number = ""

    public fun activate(
        device: UsbDevice,
        connection: UsbDeviceConnection
    ): IntegerResult {
        usbSerial = CH340G(device, connection)
        val result = usbSerial.openInterfaces()
        return when (result) {
            is IntegerResult.Success -> {
                usbSerial.start()
            }
            is IntegerResult.Error -> {
                result
            }
        }
    }

    public fun hookUp(): BooleanResult {
        val result = usbSerial.getHandshake()
        return when (result) {
            is HandshakeResult.Success -> {
                val hookIsUp = result.value.contains(GclBit.RI)
                if (!hookIsUp && number != "") {
                    number = ""
                }
                BooleanResult.Success(hookIsUp)
            }
            is HandshakeResult.Error -> {
                BooleanResult.Error(result.message)
            }
        }
    }

    public fun ring(): IntegerResult {
        return usbSerial.setHandshake(setOf(ModemBit.RTS))
    }

    public fun dialledNumber(): StringResult {
        val hookIsUp = hookUp();
        val result = when (hookIsUp) {
            is BooleanResult.Success -> {
                usbSerial.receive(1)
            }
            is BooleanResult.Error -> {
                StringResult.Error(hookIsUp.message)
            }
        }
        return when (result) {
            is StringResult.Success -> {
                number = number + result.value
                StringResult.Success(number)
            }
            is StringResult.Error -> {
                result
            }
        }
    }

    public fun numberIsValid(): Boolean {
        return validatePhoneNumber(number)
    }
}