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

        dialButton.setOnClickListener {
            phoneNumber.clear()
            dialing("02087599036")
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        tones.finish()
    }

    public override fun onStart() {
        super.onStart()
        logInfo("OutgoingActivity", "onStart")
        pollOutgoing()
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
        if (connectedIndicator.isChecked()) {
            if (hookIsUp()) {
                dialing(ch340g.readSerial())
            } else {
                noDialing()
            }
        }
        super.pollOutgoing()
    }

    private fun noDialing() {
        phoneNumber.clear()
        tones.stop()
        outputMode(ring = false, amp = false)
        numberDisplay.setText("Not dialing")
    }

    private fun dialing(digits: String) {
        val validatorResult = phoneNumber.digits(digits)
        numberDisplay.setText(phoneNumber.number())
        when (validatorResult) {
            ValidatorResult.Invalid -> {
                tones.play(ToneSelection.MISDIAL)
                outputMode(ring = false, amp = true)
            }
            ValidatorResult.Incomplete -> {
                tones.play(ToneSelection.DIAL)
                outputMode(ring = false, amp = true)
            }
            ValidatorResult.Good -> {
                tones.stop()
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:" + numberDisplay.text)
                startActivity(intent)
            }
        }
    }
}
