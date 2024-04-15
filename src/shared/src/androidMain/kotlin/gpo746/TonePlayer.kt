package andyp.gpo746

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import java.lang.Thread

enum class ToneSelection { DIAL, MISDIAL, ENGAGED }

abstract class TonePlayer {

    private var thread: Thread? = null

    protected val audioTrack: AudioTrack

    protected val bufferSize: Int = AudioTrack.getMinBufferSize(
        SAMPLE_FREQUENCY,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_8BIT
    )

    private lateinit var source: ToneSource

    protected abstract val dialTone: ToneSource

    protected abstract val engagedTone: ToneSource

    protected abstract val misdialTone: ToneSource

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

    public fun isPlaying(): Boolean =
        audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING ||
            thread != null

    public fun stop() {
        audioTrack.stop()
        audioTrack.flush()
        if (thread != null) {
            try {
                thread?.join()
            } catch (e: InterruptedException) {
                // pass
            }
            thread = null
        }
    }

    public fun play(selection: ToneSelection) {
        stop()
        source = when (selection) {
            ToneSelection.DIAL -> dialTone
            ToneSelection.MISDIAL -> misdialTone
            ToneSelection.ENGAGED -> engagedTone
        }
        thread = Thread(
            Runnable {
                source.fillBuffer()
                audioTrack.play()
                while (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    source.nextBlock()
                }
            }
        )
        thread!!.start()
    }
}
