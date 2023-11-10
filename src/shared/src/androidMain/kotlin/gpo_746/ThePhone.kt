package gpo_746

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager

class ThePhone {
    private lateinit var ch340g: Ch340g

    private val validator = PhoneNumberValidator()
    private var number: String = ""
    private var ringing: Boolean = false

    public fun setupUsb(device: UsbDevice) {
        ch340g = Ch340g(UsbSystemProduction(
            getSystemService(UsbManager::class.java),
            device
        ))
    }

    public fun start() {
        ch340g.start()
    }

    public fun finish() {
        ch340g.finish()
    }

    public function dialedNumber() {
        number = number + ch340g.readSerial()
        return number
    }

    public function numberValid() {
        return validator.good(number)
    }

    public fun hookStatus() {
        val hookIsUp = ch340g.readHandshake()
        if (!hookIsUp) {
            number = ""
        }
        return hookIsUp
    }

    public fun testRinger() {
        ring(!ringing)
    }

    public fun isRinging() {
        return ringing
    }

    public fun ring(status: Boolean) {
        ringing = status
        ch340g.writeHandshake(ringing)
    }
}
