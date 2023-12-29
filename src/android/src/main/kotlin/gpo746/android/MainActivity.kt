package andyp.gpo746.android

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.ServiceConnection
import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import andyp.gpo746.Tones

private const val ARBITRARY_REQUEST_CODE_READ_PHONE_STATE = 100

class MainActivity : Activity() {
    private val tones = Tones()

    private lateinit var hookIndicator: CheckBox
    private lateinit var numberDisplay: TextView
    private lateinit var statusDisplay: TextView

    private var phoneService: PhoneService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            Log.i("gpo746", "onServiceConnected $className")
            (binder as PhoneService.PhoneBinder).getService().callback(
                this@MainActivity::callback
            )
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.i("gpo746", "onServiceDisconnected $className")
            phoneService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hookIndicator = findViewById<CheckBox>(R.id.hookIndicator)
        numberDisplay = findViewById<TextView>(R.id.numberDisplay)
        statusDisplay = findViewById<TextView>(R.id.statusDisplay)
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

    override public fun onStart() {
        super.onStart()
        val intent = getIntent()
        val action = intent.getAction()
        Log.i("gpo746", if (action == null) "Start with no action" else action)
        if (action == "android.hardware.usb.action.USB_DEVICE_ATTACHED") {
            val bindIntent = Intent(
                this@MainActivity,
                PhoneService::class.java
            )
            bindIntent.putExtras(intent)
            bindService(bindIntent, connection, Context.BIND_AUTO_CREATE)
            /*
                    pollHandset()
            */
        } else {
            reportError("No USB device attached")
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

    public fun callback(hookIsUp: Boolean, badNumber: Boolean, number: String, uri: Uri?) {
        hookIndicator.setChecked(hookIsUp)
        numberDisplay.apply { text = number }
        if (hookIsUp && uri == null) {
            tones.play(badNumber)
        } else {
            tones.stop()
        }
        if (uri != null) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = uri
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        tones.finish()
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
    }

    private fun reportException(exception: Exception) {
        reportError(exception.toString())
    }
}
