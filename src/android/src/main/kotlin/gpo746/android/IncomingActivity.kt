package andyp.gpo746.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telephony.TelephonyManager

open class IncomingActivity : IdleActivity() {

    private val phoneStateReceiver = object : BroadcastReceiver() {

        public override fun onReceive(context: Context, intent: Intent) {
            val state: String? = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            state?.let {
                if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                    logInfo("IncomingActivity", "ringing")
                    ring(true)
                }
                if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    logInfo("IncomingActivity", "idle")
                    ring(false)
                }
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filter = IntentFilter()
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(phoneStateReceiver, filter)

        ringButton.setOnClickListener {
            ring(!ringingIndicator.isChecked())
        }
    }

    private fun ring(ringing: Boolean) {
        ringingIndicator.setChecked(ringing)
        outputMode(ringing, false)
    }
}
