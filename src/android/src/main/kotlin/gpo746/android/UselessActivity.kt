package andyp.gpo746.android

import android.app.Activity
import android.content.pm.PackageManager
import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val ARBITRARY_REQUEST_CODE_READ_PHONE_STATE = 418

/*
 * "Useless" Activity to handle scenarios when the device is useless.
 * When no permissions are granted to use Android Telephony.
 * Or the USB device is not connected.
 */
class UselessActivity : Activity() {
    private lateinit var statusDisplay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_useless)
        statusDisplay = findViewById<TextView>(R.id.statusDisplay)
    }

    override public fun onStart() {
        super.onStart()
        statusDisplay.setText(
            "GPO 746 Handset\n\nCan't communicate with phone because the USB is not connected"
        )
        checkPermissions()
    }

    private fun checkPermissions() {
        Log.i("gpo746", "checking permissions")
        val activity = this@UselessActivity
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

    override public fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ARBITRARY_REQUEST_CODE_READ_PHONE_STATE) {
            val granted = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (granted) {
                Log.i("gpo746", "permissions granted")
            } else {
                Log.i("gpo746", "permissions not granted")
                statusDisplay.append(
                    "\n\nCan't answer calls because permissions are not granted"
                )
            }
        }
    }

}
