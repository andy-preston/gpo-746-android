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
import gpo_746.ThePhone
import gpo_746.Tones

class MainActivity : Activity() {

    private val thePhone = ThePhone()

    private lateinit var hookIndicator: CheckBox
    private lateinit var validIndicator: CheckBox
    private lateinit var ringButton: Button
    private lateinit var toneDialButton: Button
    private lateinit var toneMisdialButton: Button
    private lateinit var numberDisplay: TextView
    private lateinit var statusDisplay: TextView

    private val delayMilliseconds: Long = 1000
    private var noErrors: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutElements()
        val device = getIntent().getParcelableExtra<UsbDevice>(
            UsbManager.EXTRA_DEVICE
        )
        if (device == null) {
            reportError("Failed to get device from intent")
        } else {
            thePhone.setupUsb(device, getSystemService(UsbManager::class.java))
        }
        registerReceiver(
            detachReceiver,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        )
        try {
            if (noErrors) {
               thePhone.start()
            }
        } catch (e: Exception) {
            reportException(e)
        }
        startWorking()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(detachReceiver)
        thePhone.finish()
    }

    private val detachReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {
                reportError("detachReceiver - should finish now.")
                finish()
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
        ringButton.setOnClickListener {
            if (noErrors) {
                thePhone.testRinger()
                statusDisplay.apply {
                    text = if (thePhone.isRinging()) "RINGING" else "WAITING"
                }
            }
        }
        toneDialButton.setOnClickListener {
            thePhone.testDialTone()
        }
        toneMisdialButton.setOnClickListener {
            thePhone.testMisdialTone()
        }
        pollHandset()
    }

    private fun pollHandset() {
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                if (noErrors) {
                    hookIndicator.setChecked(thePhone.hookStatus())
                    numberDisplay.apply { text = thePhone.dialledNumber() }
                    validIndicator.setChecked(thePhone.numberValid())
                    pollHandset()
                }
            } catch (e: Exception) {
                reportException(e)
            }
        }, delayMilliseconds)
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
