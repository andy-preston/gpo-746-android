package com.gitlab.edgeeffect.gpo746

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

class MainActivity : /*AppCompat*/Activity() {

    private lateinit var usbManager: UsbManager
    private lateinit var connection: UsbDeviceConnection
    private lateinit var usbSerial: CH340G

    private lateinit var numberDisplay: TextView
    private lateinit var hookIndicator: CheckBox
    private lateinit var validIndicator: CheckBox

    private var noErrors = true
    private var hookIsUp = false
    private var number = ""

    private fun reportError(message: String) {
        numberDisplay.apply { text = message }
        noErrors = false
    }

    private val detachReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == intent.action) {
                connection.close()
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

    private fun hookStatus() {
        val result = usbSerial.getHandshake()
        when (result) {
            is HandshakeResult.Success -> {
                hookIsUp = result.value.contains(GclBit.RI)
                hookIndicator.setChecked(hookIsUp)
                if (!hookIsUp && number != "") {
                    number = ""
                }
            }
            is HandshakeResult.Error -> {
                reportError(result.message)
            }
        }
    }

    private fun dialledNumber() {
        val result = usbSerial.receive(1)
        when (result) {
            is StringResult.Success -> {
                number = number + result.value
                numberDisplay.apply { text = number }
                validIndicator.setChecked(validatePhoneNumber(number))
            }
            is StringResult.Error -> {
                reportError(result.message)
            }
        }
    }

    private fun pollHandset() {
        Handler(Looper.getMainLooper()).postDelayed({
            hookStatus()
            dialledNumber()
            if (noErrors) {
                pollHandset()
            }
        }, 3_000)
    }

    private fun ringButton() {
        val button = findViewById<Button>(R.id.ringButton)
        var ringing = false
        button.setOnClickListener {
            ringing = !ringing
            val result = usbSerial.setHandshake(
                if (ringing) setOf(ModemBit.RTS) else setOf<ModemBit>()
            )
            when (result) {
                is IntegerResult.Success -> {
                    numberDisplay.apply {
                        text = if (ringing) "RINGING" else "WAITING"
                    }
                }
                is IntegerResult.Error -> {
                    reportError(result.message)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        numberDisplay = findViewById<TextView>(R.id.numberDisplay)
        usbManager = getSystemService(UsbManager::class.java)
        val device = getDevice(getIntent())
        connection = usbManager.openDevice(device)
        registerReceiver(
            detachReceiver,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        )
        usbSerial = CH340G(device, connection)
        val started = usbSerial.start()
        when (started) {
            is IntegerResult.Success -> {
                hookIndicator = findViewById<CheckBox>(R.id.hookIndicator)
                validIndicator = findViewById<CheckBox>(R.id.validIndicator)
                ringButton()
                pollHandset()
            }
            is IntegerResult.Error -> {
                reportError(started.message)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(detachReceiver)
    }

}
