package andyp.gpo746

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager

@Suppress("TooManyFunctions")
class ThePhone {
    private lateinit var ch340g: Ch340g
    private val tones = Tones()

    private val validator = PhoneNumberValidator()
    private var number: String = ""
    private var ringing: Boolean = false

    public fun setupUsb(device: UsbDevice, usbManager: UsbManager) {
        ch340g = Ch340g(UsbSystemProduction(device, usbManager))
    }

    public fun start() {
        ch340g.start()
        tones.start()
    }

    public fun finish() {
        tones.finish()
        ch340g.finish()
    }

    public fun dialledNumber(): String {
        number = number + ch340g.readSerial()
        return number
    }

    public fun numberValid(): Boolean {
        val result = validator.result(number)
        if (result == ValidatorResult.Good) {
            tones.stopPlaying()
            return true
        }
        if (result == ValidatorResult.Invalid) {
            tones.playMisdialTone()
        }
        return false
    }

    public fun hookStatus(): Boolean {
        val hookIsUp = ch340g.readHandshake()
        if (hookIsUp) {
            tones.playDialTone()
        } else {
            number = ""
            tones.stopPlaying()
        }
        return hookIsUp
    }

    public fun isRinging(): Boolean {
        return ringing
    }

    public fun ring(status: Boolean) {
        ringing = status
        ch340g.writeHandshake(ringing)
    }

    public fun testRinger() {
        ring(!ringing)
    }

    public fun testDialTone() {
        tones.testDialTone()
    }

    public fun testMisdialTone() {
        tones.testMisdialTone()
    }
}
