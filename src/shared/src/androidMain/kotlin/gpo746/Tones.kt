package andyp.gpo746

import android.media.AudioFormat
import android.media.AudioTrack
import java.lang.Thread

enum class ToneSelection { DIAL, MISDIAL, ENGAGED }

final class Tones : ToneData() {
    private var thread: Thread? = null
    private var playing: ToneSelection? = null
    private val dialTone: Tone
    private val misdialTone: Tone
    private val engagedTone: Tone

    init {
        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_FREQUENCY,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_8BIT
        )
        dialTone = Tone(minBufferSize, dialToneData)
        misdialTone = Tone(minBufferSize, misdialToneData)
        engagedTone = Tone(minBufferSize, engagedToneData)
    }

    public fun finish() {
        stop()
        misdialTone.release()
        dialTone.release()
        engagedTone.release()
    }

    public fun isPlaying(): Boolean {
        return playing != null || thread != null
    }

    public fun stop() {
        playing = null
        try {
            thread?.join()
        } catch (e: InterruptedException) {
            // pass
        }
        thread = null
    }

    public fun play(selection: ToneSelection) {
        if (selection == playing) {
            return
        }
        val tone = when (selection) {
            ToneSelection.DIAL -> dialTone
            ToneSelection.MISDIAL -> misdialTone
            ToneSelection.ENGAGED -> engagedTone
        }
        stop()
        playing = selection
        Thread(
            Runnable {
                tone.write()
                tone.start()
                while (playing != null) {
                    tone.write()
                }
                tone.stop()
            }
        ).start()
    }
}
