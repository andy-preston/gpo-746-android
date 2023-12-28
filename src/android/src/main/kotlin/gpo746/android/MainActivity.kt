package andyp.gpo746.android

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
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import andyp.gpo746.ThePhone

private const val DELAY_MILLISECONDS: Long = 1000

class MainActivity : Activity() {

    private val thePhone = ThePhone()
    private var somethingWrong: Boolean = false

    private lateinit var hookIndicator: CheckBox
    private lateinit var validIndicator: CheckBox
    private lateinit var ringButton: Button
    private lateinit var toneDialButton: Button
    private lateinit var toneMisdialButton: Button
    private lateinit var numberDisplay: TextView
    private lateinit var statusDisplay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutElements()
        setupUsb()
        try {
            if (!somethingWrong) thePhone.start()
        } catch (e: Exception) {
            reportException(e)
        }
        setupReceivers()
        listenToUI()
        pollHandset()
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

    private fun listenToUI() {
        ringButton.setOnClickListener {
            if (!somethingWrong) {
                thePhone.testRinger()
                displayStatus(if (thePhone.isRinging()) "RINGING" else "WAITING")
            }
        }
        toneDialButton.setOnClickListener {
            thePhone.testDialTone()
        }
        toneMisdialButton.setOnClickListener {
            thePhone.testMisdialTone()
        }
    }

    private fun setupReceivers() {
        registerReceiver(
            incomingReceiver,
            IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        )
    }

    private val incomingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state: String? = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == null) {
                return
            }
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                // start ringing
                // sound off
            }
            if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                // stop ringing
                // sound on
            }
            if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                // stop ringing
                // sound off
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
            thePhone.setupUsb(device, getSystemService(UsbManager::class.java))
        }
    }

    private fun pollHandset() {
        if (somethingWrong) {
            return
        }
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                if (thePhone.hookStatus()) {
                    hookIndicator.setChecked(true)
                } else {
                    hookIndicator.setChecked(false)
                    displayNumber("")
                }
                if (thePhone.numberValid()) {
                    validIndicator.setChecked(true)
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = thePhone.uri()
                    startActivity(intent)
                } else {
                    validIndicator.setChecked(false)
                    displayNumber(thePhone.dialledNumber())
                }
                pollHandset()
            } catch (e: Exception) {
                reportException(e)
            }
        }, DELAY_MILLISECONDS)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(incomingReceiver)
        thePhone.finish()
    }

    private fun displayNumber(number: String) {
        numberDisplay.apply { text = number }
    }

    private fun displayStatus(message: String) {
        statusDisplay.apply { text = message }
    }

    private fun logStatus(message: String) {
        Log.i("gpo746", message)
        displayStatus(message)
    }

    private fun reportError(message: String) {
        Log.e("gpo746", message)
        displayStatus(message)
        somethingWrong = true
    }

    private fun reportException(exception: Exception) {
        reportError(exception.toString())
    }
}
