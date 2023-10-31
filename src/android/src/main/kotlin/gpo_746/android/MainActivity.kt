package gpo_746.android

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import gpo_746.Ch340g
import gpo_746.PhoneNumberValidator
import gpo_746.UsbSystemProduction

class MainActivity : Activity() {

    private lateinit var ch340g: Ch340g
    private val validator = PhoneNumberValidator()

    private lateinit var numberDisplay: TextView
    private lateinit var hookIndicator: CheckBox
    private lateinit var validIndicator: CheckBox
    private lateinit var ringButton: Button

    private var ringing = false
    private var hookIsUp = false
    private var number = ""
    private var noErrors = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        numberDisplay = findViewById<TextView>(R.id.numberDisplay)
        numberDisplay.apply { text = "placeholder" }

        ch340g = Ch340g(
            UsbSystemProduction(
                getSystemService(UsbManager::class.java),
                deviceFromIntent(getIntent())
            )
        )
        registerReceiver(
            detachReceiver,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        )
        try {
            /*val started =*/ ch340g.open()
        } catch(e: Exception) {
            reportException(e)
            return
        }
        hookIndicator = findViewById<CheckBox>(R.id.hookIndicator)
        validIndicator = findViewById<CheckBox>(R.id.validIndicator)
        ringButton = findViewById<Button>(R.id.ringButton)
        ringButtonListen()
        pollHandset()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(detachReceiver)
    }

    private fun deviceFromIntent(intent: Intent): UsbDevice {
        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
        if (device == null) {
            throw Exception("Failed to get device from intent")
        } else {
            return device
        }
    }

    private val detachReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {
                ch340g.close()
                finish()
            }
        }
    }

    private fun ringButtonListen() {
        ringButton.setOnClickListener {
            if (noErrors) {
                ringing = !ringing
                try {
                    ch340g.writeHandshake(ringing)
                    numberDisplay.apply {
                        text = if (ringing) "RINGING" else "WAITING"
                    }
                } catch(e: Exception) {
                    reportException(e)
                }
            }
        }
    }

    private fun pollHandset() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (noErrors) {
                hookStatus()
            }
            if (noErrors) {
                dialledNumber()
            }
            if (noErrors) {
                pollHandset()
            }
        }, 3_000)
    }

    private fun hookStatus() {
        try {
            hookIsUp = ch340g.readHandshake()
            hookIndicator.setChecked(hookIsUp)
            if ((!hookIsUp) && (number != "")) {
                number = ""
            }
        } catch (e: Exception) {
            reportException(e)
        }
    }

    private fun dialledNumber() {
        try {
            number = number + ch340g.readSerial()
            numberDisplay.apply { text = number }
            validIndicator.setChecked(validator.good(number))
        } catch (e: Exception) {
            reportException(e)
        }
    }

    private fun reportError(message: String) {
        numberDisplay.apply { text = message }
        noErrors = false
    }

    private fun reportException(exception: Exception) {
        reportError(exception.toString())
    }
}
