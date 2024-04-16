package andyp.gpo746.android

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telecom.TelecomManager
import android.telephony.TelephonyManager

abstract class IncomingActivity : PollingActivity() {

    private var callInProgress: Boolean = false

    private val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    private val phoneStateReceiver = object : BroadcastReceiver() {
        public override fun onReceive(context: Context, intent: Intent) {
            val state: String? = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            state?.let {
                if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                    logInfo("IncomingActivity", "ringing")
                    pollIncoming()
                    ring(true)
                }
                if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    logInfo("IncomingActivity", "idle")
                    pollOutgoing()
                    ring(false)
                }
                callInProgress = state == TelephonyManager.EXTRA_STATE_OFFHOOK
            }
        }
    }

    protected override fun pollIncoming() {
        logInfo("IncomingActivity", "pollIncoming")
        if (hookIsUp()) {
            acceptRingingCall()
        } else {
            endCall()
        }
        super.pollIncoming()
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

    // I'm suppressing the lint here because the Android linter hasn't got the
    // brains to see what `allAlreadyGranted` does.
    @SuppressLint("MissingPermission")
    private fun acceptRingingCall() {
        if (!callInProgress && allAlreadyGranted()) {
            // Yeah... I know this is deprecated but it's going to have to do
            // because the alternative is hugely over-complicated and, at least
            // for now, I just want to get it working.
            @Suppress("DEPRECATION")
            telecomManager.acceptRingingCall()
        }
    }

    @SuppressLint("MissingPermission")
    private fun endCall() {
        if (callInProgress && allAlreadyGranted()) {
            @Suppress("DEPRECATION")
            telecomManager.endCall()
        }
    }
}
