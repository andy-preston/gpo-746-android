package andyp.gpo746.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import andyp.gpo746.PhoneNumberValidator
import andyp.gpo746.Tones
import andyp.gpo746.ValidatorResult

private const val DELAY_MILLISECONDS: Long = 1000

open class OutgoingActivity : IncomingActivity() {

    private val tones = Tones()
    private val validator = PhoneNumberValidator()
    private var number: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toneDialButton.setOnClickListener {
            if (tones.isPlaying()) {
                tones.stop()
            } else {
                tones.play(false)
            }
        }
        toneMisdialButton.setOnClickListener {
            if (tones.isPlaying()) {
                tones.stop()
            } else {
                tones.play(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tones.finish()
    }

    public fun poll() {
        logInfo("OutgoingActivity", "Poll")
        if (hookIsUp()) {
            number + ch340g.readSerial()
        } else {
            number = ""
            tones.stop()
        }
        numberDisplay.apply { text = number }
        when (validator.result(number)) {
            ValidatorResult.Invalid -> invalidNumber()
            ValidatorResult.Incomplete -> incompleteNumber()
            ValidatorResult.Good -> dialNumber()
        }
        Handler(Looper.getMainLooper()).postDelayed(
            { poll() },
            "OutgoingActivity",
            DELAY_MILLISECONDS
        )
    }

    private fun invalidNumber() {
        tones.play(true)
    }

    private fun incompleteNumber() {
        tones.play(false)
    }

    private fun dialNumber() {
        tones.stop()
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }
}
