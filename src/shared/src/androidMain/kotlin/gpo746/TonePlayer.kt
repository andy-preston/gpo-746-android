package andyp.gpo746

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import java.lang.Thread

enum class ToneSelection { DIAL, MISDIAL, ENGAGED }

abstract class TonePlayer : ToneBufferFiller() {

    private var thread: Thread? = null

    private var sampleSource: ByteArray? = null

    private val audioTrack: AudioTrack

    protected abstract val dialSamples: ByteArray

    protected abstract val engagedSamples: ByteArray

    protected abstract val misdialSamples: ByteArray

    protected override val bufferSize: Int = AudioTrack.getMinBufferSize(
        SAMPLE_FREQUENCY,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_8BIT
    )

    init {
        val audioAttributes = AudioAttributes.Builder().setUsage(
            AudioAttributes.USAGE_MEDIA
        ).setContentType(
            AudioAttributes.CONTENT_TYPE_SONIFICATION
        ).build()

        val audioFormat = AudioFormat.Builder().setEncoding(
            AudioFormat.ENCODING_PCM_8BIT
        ).setChannelMask(
            AudioFormat.CHANNEL_OUT_MONO
        ).setSampleRate(
            SAMPLE_FREQUENCY
        ).build()

        audioTrack = AudioTrack(
            audioAttributes,
            audioFormat,
            bufferSize,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
    }

    public fun finish() {
        stop()
        audioTrack.release()
    }

    public fun isPlaying(): Boolean = (sampleSource != null || thread != null)

    public fun stop() {
        sampleSource = null
        try {
            thread?.join()
        } catch (e: InterruptedException) {
            // pass
        }
        thread = null
    }

    public fun play(selection: ToneSelection) {
        sampleSource = when (selection) {
            ToneSelection.DIAL -> dialSamples
            ToneSelection.MISDIAL -> misdialSamples
            ToneSelection.ENGAGED -> engagedSamples
        }
        stop()
        Thread(
            Runnable {
                audioTrack.write(setupSamples(sampleSource!!), 0, bufferSize)
                audioTrack.play()
                while (sampleSource != null) {
                    audioTrack.write(setupSamples(sampleSource!!), 0, bufferSize)
                }
                audioTrack.stop()
            }
        ).start()
    }
}
