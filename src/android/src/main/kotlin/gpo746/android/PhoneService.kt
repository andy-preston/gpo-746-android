package andyp.gpo746.android

import android.app.Service
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import andyp.gpo746.Ch340g
import andyp.gpo746.PhoneNumberValidator
import andyp.gpo746.UsbSystemProduction
import andyp.gpo746.ValidatorResult

private const val DELAY_MILLISECONDS: Long = 1000

class PhoneService : Service() {

    private val binder = PhoneBinder()
    private val validator = PhoneNumberValidator()
    private var number: String = ""

    private lateinit var ch340g: Ch340g
    private lateinit var callbackFun: ((Boolean, String, Uri?)->Unit)

    inner class PhoneBinder : Binder() {
        fun getService(): PhoneService = this@PhoneService
    }

    override fun onCreate() {
        Log.i("gpo746", "service onCreate")
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i("gpo746", "service onBind")
        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
        if (device == null) {
            Log.e("gpo746", "PhoneService - device was null")
        } else if (this::ch340g.isInitialized) {
            Log.e("gpo746", "PhoneService - device already initialised")
        } else {
            Log.i("gpo746", "PhoneService - initialising device")
            ch340g = Ch340g(
                UsbSystemProduction(
                    device,
                    getSystemService(UsbManager::class.java)
                )
            )
            ch340g.start()
            // pollHandset
        }
        return binder
    }

    override fun onDestroy() {
        Log.i("gpo746", "PhoneService - destroy")
        ch340g.finish()
    }

    public fun callback(callback: ((Boolean, String, Uri?)->Unit)) {
        callbackFun = callback
    }

    public fun poll() {
        Log.i("gpo746", "PhoneService - poll")
        val hookIsUp = ch340g.readHandshake()
        number = if (hookIsUp) number + ch340g.readSerial() else ""
        val validity = validator.result(number)
        val uri: Uri? = if (validity == ValidatorResult.Good) Uri.parse("tel:$number") else null
        if (uri != null) number = ""
        Handler(Looper.getMainLooper()).postDelayed({
            callbackFun.invoke(hookIsUp, if (uri == null) number else "", uri)
            poll()
        }, DELAY_MILLISECONDS)
    }

    public fun ring(ringing: Boolean) {
        ch340g.writeHandshake(ringing)
    }
}