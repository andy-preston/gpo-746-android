package andyp.gpo746.android

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.telephony.TelephonyManager
import android.util.Log

class IncomingCall: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val state: String? = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        state?.let {
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                Log.i("gpo746", "ringing")
            }
            if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                Log.i("gpo746", "idle")
            }
        }
    }
}
