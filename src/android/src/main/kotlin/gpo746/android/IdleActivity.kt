package andyp.gpo746.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import andyp.gpo746.Ch340g

open class IdleActivity : PermissionActivity() {

    private val usbHelper = UsbHelper()
    protected val ch340g = Ch340g(usbHelper)

    val disconnectedReceiver = object : BroadcastReceiver() {
        public override fun onReceive(context: Context, intent: Intent) {
            deviceDetached()
        }
    }

    public override fun onStart() {
        super.onStart()
        logInfo("IdleActivity", "onStart check it works for USB-connect and launch")
        onNewIntent(getIntent())
    }

    protected override fun onNewIntent(intent: Intent) {
        super.onNewIntent()
        val action = intent.getAction()
        val logAction = if (action == null) "No action" else "Action: $action"
        logInfo("IdleActivity", "OnNewIntent - $logAction")
        if (action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            deviceAttached(intent)
        }
    }

    private fun deviceDetached() {
        logInfo("IdleActivity", "deviceDetached")
        connectedIndicator.setChecked(false)
        usbHelper.closeDevice()
        unregisterReceiver(disconnectedReceiver)
    }

    private fun deviceAttached(intent: Intent) {
        logInfo("IdleActivity", "deviceAttached")
        usbHelper.openDevice(
            intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE),
            getSystemService(UsbManager::class.java)
        )
        ch340g.start()
        logInfo("IdleActivity", "Register receiver")
        val filter = IntentFilter()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(disconnectedReceiver, filter)
        connectedIndicator.setChecked(true)
    }

    protected fun hookIsUp(): Boolean {
        val hookUp = ch340g.readHandshake()
        hookIndicator.setChecked(hookUp)
        return hookUp
    }
}
