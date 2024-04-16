package andyp.gpo746.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import andyp.gpo746.PhoneNumber
import andyp.gpo746.ToneSelection
import andyp.gpo746.Tones
import andyp.gpo746.ValidatorResult

abstract class OutgoingActivity : IncomingActivity() {

    private val tones = Tones()
    private val phoneNumber = PhoneNumber()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toneDialButton.setTag(ToneSelection.DIAL)
        toneDialButton.setOnClickListener(toneClickListener)
        toneMisdialButton.setTag(ToneSelection.MISDIAL)
        toneMisdialButton.setOnClickListener(toneClickListener)
        toneEngagedButton.setTag(ToneSelection.ENGAGED)
        toneEngagedButton.setOnClickListener(toneClickListener)
    }

    public override fun onDestroy() {
        super.onDestroy()
        tones.finish()
    }

    public override fun onStart() {
        super.onStart()
        logInfo("OutgoingActivity", "onStart")
        //pollOutgoing()
    }

    private val toneClickListener = object : View.OnClickListener {
        override fun onClick(view: View?) {
            if (tones.isPlaying()) {
                tones.stop()
            } else {
                tones.play(view!!.getTag() as ToneSelection)
            }
        }
    }

    protected override fun pollOutgoing() {
        logInfo("OutgoingActivity", "pollOutgoing")
        if (hookIsUp()) {
            when (phoneNumber.digits(ch340g.readSerial())) {
                ValidatorResult.Invalid -> invalidNumber()
                ValidatorResult.Incomplete -> incompleteNumber()
                ValidatorResult.Good -> dialNumber()
            }
        } else {
            phoneNumber.clear()
            tones.stop()
            outputMode(ring = false, amp = false)
        }
        numberDisplay.apply { text = phoneNumber.number() }
        super.pollOutgoing()
    }

    private fun invalidNumber() {
        tones.play(ToneSelection.MISDIAL)
        outputMode(ring = false, amp = true)
    }

    private fun incompleteNumber() {
        tones.play(ToneSelection.DIAL)
        outputMode(ring = false, amp = true)
    }

    private fun dialNumber() {
        tones.stop()
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:" + phoneNumber.number())
        startActivity(intent)
    }
}
