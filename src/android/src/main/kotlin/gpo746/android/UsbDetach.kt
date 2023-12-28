package andyp.gpo746.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.ActivityManager
import android.os.Process

class UsbDetach: BroadcastReceiver() {
    override public fun onReceive(context: Context, intent: Intent) {
        val pid = Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getRunningAppProcesses().filter {
            it.pid != pid
        }.forEach {
            Process.killProcess(it.pid)
        }
        Process.killProcess(pid)
    }
}
