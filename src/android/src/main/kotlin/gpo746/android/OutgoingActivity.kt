package andyp.gpo746.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import andyp.gpo746.PhoneNumberValidator
import andyp.gpo746.Tones
import andyp.gpo746.ToneSelection
import andyp.gpo746.ValidatorResult

private const val LOOPER_DELAY_MILLISECONDS: Long = 1000

open class OutgoingActivity : IncomingActivity() {

    private val tones = Tones()
    private val validator = PhoneNumberValidator()
    private var number: String = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toneDialButton.setOnClickListener {
            if (tones.isPlaying()) {
                tones.stop()
            } else {
                tones.play(ToneSelection.dial)
            }
        }
        toneMisdialButton.setOnClickListener {
            if (tones.isPlaying()) {
                tones.stop()
            } else {
                tones.play(ToneSelection.misdial)
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        tones.finish()
    }

    public fun poll() {
        logInfo("OutgoingActivity", "Poll")
        if (hookIsUp()) {
            number + dialledDigits()
        } else {
            number = ""
            tones.stop()
            outputMode(ring = false, amp = false)
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
            LOOPER_DELAY_MILLISECONDS
        )
    }

    private fun invalidNumber() {
        tones.play(ToneSelection.misdial)
        outputMode(ring = false, amp = true)
    }

    private fun incompleteNumber() {
        tones.play(ToneSelection.dial)
        outputMode(ring = false, amp = true)
    }

    private fun dialNumber() {
        tones.stop()
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }
}
