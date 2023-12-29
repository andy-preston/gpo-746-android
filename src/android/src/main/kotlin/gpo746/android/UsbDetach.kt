package andyp.gpo746.android

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log

class UsbDetach : BroadcastReceiver() {
    public override fun onReceive(context: Context, intent: Intent) {
        val pid = Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getRunningAppProcesses().filter {
            it.pid != pid
        }.forEach {
            Log.i("gpo746", "closing other process ${it.pid}")
            Process.killProcess(it.pid)
        }
        Log.i("gpo746", "closing main process $pid")
        Process.killProcess(pid)
    }
}
