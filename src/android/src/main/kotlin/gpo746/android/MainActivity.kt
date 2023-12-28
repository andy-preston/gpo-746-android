package andyp.gpo746.android

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import andyp.gpo746.ThePhone

private const val DELAY_MILLISECONDS: Long = 1000
private const val ARBITRARY_REQUEST_CODE_READ_PHONE_STATE = 100

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
        setContentView(R.layout.activity_main)
        layoutElements()
        getPermissions()
        setupUsb()
        try {
            if (!somethingWrong) thePhone.start()
        } catch (e: Exception) {
            reportException(e)
        }
        listenToUI()
        pollHandset()
    }

    private fun getPermissions() {
        val activity = this@MainActivity
        val permission = Manifest.permission.READ_PHONE_STATE
        val grantState = ContextCompat.checkSelfPermission(activity, permission)
        if (grantState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                ARBITRARY_REQUEST_CODE_READ_PHONE_STATE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ARBITRARY_REQUEST_CODE_READ_PHONE_STATE) {
            val granted = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                reportError("No permissions")
            }
        }
    }

    private fun layoutElements() {
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
