package andyp.gpo746.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telephony.TelephonyManager

open class IncomingActivity : IdleActivity() {

    private var ringing: Boolean = false

    val phoneStateReceiver = object : BroadcastReceiver() {
        override public fun onReceive(context: Context, intent: Intent) {
            val state: String? = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            state?.let {
                if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                    logInfo("IncomingActivity", "ringing")
                    ringingIndicator.setChecked(true)
                    ch340g.writeHandshake(true)
                }
                if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    logInfo("IncomingActivity", "idle")
                    ringingIndicator.setChecked(false)
                    ch340g.writeHandshake(false)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter()
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(phoneStateReceiver, filter)
        ringButton.setOnClickListener {
            if (connectedIndicator.isChecked()) {
                ring(!ringingIndicator.isChecked())
            }
        }
    }

    private fun ring(ringing: Boolean) {
        ringingIndicator.setChecked(ringing)
        ch340g.writeHandshake(ringing)
    }
}

