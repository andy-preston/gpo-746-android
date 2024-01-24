package andyp.gpo746.android

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

open class UselessActivity : Activity() {

    protected lateinit var permissionIndicator: CheckBox
    protected lateinit var connectedIndicator: CheckBox
    protected lateinit var ringingIndicator: CheckBox
    protected lateinit var hookIndicator: CheckBox
    protected lateinit var ringButton: Button
    protected lateinit var toneDialButton: Button
    protected lateinit var toneMisdialButton: Button
    protected lateinit var numberDisplay: TextView
    protected lateinit var statusDisplay: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logInfo("UselessActivity", "STARTED")
        setContentView(R.layout.activity)
        permissionIndicator = findViewById<CheckBox>(R.id.permissionIndicator)
        connectedIndicator = findViewById<CheckBox>(R.id.connectedIndicator)
        ringingIndicator = findViewById<CheckBox>(R.id.ringIndicator)
        hookIndicator = findViewById<CheckBox>(R.id.hookIndicator)
        ringButton = findViewById<Button>(R.id.ringButton)
        toneDialButton = findViewById<Button>(R.id.toneDialButton)
        toneMisdialButton = findViewById<Button>(R.id.toneMisdialButton)
        numberDisplay = findViewById<TextView>(R.id.numberDisplay)
        statusDisplay = findViewById<TextView>(R.id.statusDisplay)
    }

    protected fun logInfo(source: String, message: String) {
        Log.i("gpo746", "$source - $message")
    }
}
