package gpo_746.android

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var numberDisplay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        numberDisplay = findViewById<TextView>(R.id.numberDisplay)
        numberDisplay.apply { text = "placeholder" }
    }
}

