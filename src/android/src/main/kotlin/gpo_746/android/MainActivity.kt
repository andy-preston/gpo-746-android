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
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import gpo_746.Ch340g
import gpo_746.PhoneNumberValidator
import gpo_746.ToneDial
import gpo_746.ToneMisdial
import gpo_746.UsbSystemProduction

class MainActivity : Activity() {

    private lateinit var ch340g: Ch340g
    private val validator = PhoneNumberValidator()
    private val toneDial = ToneDial()
    private val toneMisdial = ToneMisdial()

    private lateinit var hookIndicator: CheckBox
    private lateinit var validIndicator: CheckBox
    private lateinit var ringButton: Button
    private lateinit var toneDialButton: Button
    private lateinit var toneMisdialButton: Button
    private lateinit var numberDisplay: TextView
    private lateinit var statusDisplay: TextView

    private val delayMilliseconds: Long = 1000

    private var ringing: Boolean = false
    private var hookIsUp: Boolean = false
    private var noErrors: Boolean = true

    private var number: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutElements()
        setupUsb()
        registerReceiver(
            detachReceiver,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        )
        startUsb()
        setupTones()
        startWorking()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(detachReceiver)
        toneMisdial.finish()
        toneDial.finish()
        ch340g.finish()
    }

    private val detachReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {
                reportError("detachReceiver - should finish now.")
                finish()
            }
        }
    }

    private fun setupUsb() {
        val device = getIntent().getParcelableExtra<UsbDevice>(
            UsbManager.EXTRA_DEVICE
        )
        if (device == null) {
            reportError("Failed to get device from intent")
        } else {
            ch340g = Ch340g(UsbSystemProduction(
                getSystemService(UsbManager::class.java),
                device
            ))
        }
    }

    private fun setupTones() {
        try {
            toneDial.start()
            toneMisdial.start()
        } catch (e: Exception) {
            reportException(e)
        }
    }

    private fun startUsb() {
        if (noErrors) {
            try {
                ch340g.start()
            } catch(e: Exception) {
                reportException(e)
            }
        }
    }

    private fun layoutElements() {
        setContentView(R.layout.activity_main)
        hookIndicator = findViewById<CheckBox>(R.id.hookIndicator)
        validIndicator = findViewById<CheckBox>(R.id.validIndicator)
        ringButton = findViewById<Button>(R.id.ringButton)
        toneDialButton = findViewById<Button>(R.id.toneDialButton)
        toneMisdialButton = findViewById<Button>(R.id.toneMisdialButton)
        numberDisplay = findViewById<TextView>(R.id.numberDisplay)
        statusDisplay = findViewById<TextView>(R.id.statusDisplay)
    }

    private fun startWorking() {
        if (noErrors) {
            ringButtonListen()
            toneDialButtonListen()
            toneMisdialButtonListen()
            pollHandset()
        }
    }

    private fun ringButtonListen() {
        ringButton.setOnClickListener {
            if (noErrors) {
                ringing = !ringing
                try {
                    ch340g.writeHandshake(ringing)
                    statusDisplay.apply {
                        text = if (ringing) "RINGING" else "WAITING"
                    }
                } catch(e: Exception) {
                    reportException(e)
                }
            }
        }
    }

    /* This is only here for testing
       It should play when you pick up the receiver */
    private fun toneDialButtonListen() {
        toneDialButton.setOnClickListener {
            if (toneDial.isPlaying()) {
                toneDial.stop()
            } else {
                if (toneMisdial.isPlaying()) {
                    toneMisdial.stop()
                }
                toneDial.play()
            }
        }
    }

    /* This is only here for testing
       It should play when you mess-up dialing */
    private fun toneMisdialButtonListen() {
        toneMisdialButton.setOnClickListener {
            if (toneMisdial.isPlaying()) {
                toneMisdial.stop()
            } else {
                if (toneDial.isPlaying()) {
                    toneDial.stop()
                }
                toneMisdial.play()
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
        }, delayMilliseconds)
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
        Log.e("gpo746", message)
        statusDisplay.apply { text = message }
        noErrors = false
    }

    private fun reportException(exception: Exception) {
        reportError(exception.toString())
    }
}
