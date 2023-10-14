package gpo_746.android

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.TextView
import gpo_746.UsbSystemProduction
import gpo_746.Ch340g

class MainActivity : Activity() {

    private lateinit var ch340g: Ch340g

    private lateinit var numberDisplay: TextView

    public fun deviceFromIntent(intent: Intent): UsbDevice {
        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
        if (device == null) {
            throw Exception("Failed to get device from intent")
        } else {
            return device
        }
    }

    private val detachReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {
                ch340g.close()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        numberDisplay = findViewById<TextView>(R.id.numberDisplay)
        numberDisplay.apply { text = "placeholder" }

        ch340g = Ch340g(
            UsbSystemProduction(
                getSystemService(UsbManager::class.java),
                deviceFromIntent(getIntent())
            )
        )
        registerReceiver(
            detachReceiver,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        )
        ch340g.open()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(detachReceiver)
    }
}

