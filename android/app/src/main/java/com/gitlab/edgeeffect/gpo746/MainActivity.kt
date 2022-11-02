package com.gitlab.edgeeffect.gpo746

import androidx.appcompat.app.AppCompatActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.Intent
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var usbManager: UsbManager
    private lateinit var textView: TextView

    private val detachReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {
                finish()
            }
        }
    }

    private fun getDevice(intent: Intent): UsbDevice {
        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
        if (device == null) {
            throw Exception("Failed to get device from intent")
        } else {
            return device
        }
    }

    private fun openDevice(device: UsbDevice) {
        val connection: UsbDeviceConnection = usbManager.openDevice(device)
        try {
            val usbSerial = UsbSerial(device, connection)
            val result = usbSerial.openInterfaces()
            when (result) {
                is MyResult.Success -> {
                    val vendorId = device.getVendorId()
                    val productId = device.getProductId()
                    textView.apply { text = "$vendorId - $productId" }
                }
                is MyResult.Error -> {
                    textView.apply { text = result.message }
                }
            }
        } finally {
            connection.close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById<TextView>(R.id.text_view)
        usbManager = getSystemService(UsbManager::class.java)

        registerReceiver(
            detachReceiver,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        )
        openDevice(getDevice(getIntent()))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(detachReceiver)
    }

}
