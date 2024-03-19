package andyp.gpo746

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import java.io.ByteArrayOutputStream
import java.lang.Thread

enum class ToneSelection { DIAL, MISDIAL, ENGAGED }

internal final class Tone(
    sampleArray: ByteArray,
    audioAttributes: AudioAttributes,
    audioFormat: AudioFormat
) {

    private val samples = sampleArray

    private val track = AudioTrack(
        audioAttributes,
        audioFormat,
        samples.size,
        AudioTrack.MODE_STREAM,
        AudioManager.AUDIO_SESSION_ID_GENERATE
    )

    public fun release() = track.release()

    public fun write() = track.write(samples, 0, samples.size)

    public fun start() = track.play()

    public fun stop() = track.stop()
}

abstract class ToneBufferBuilder {

    protected fun setupSamples(minimumSize: Int, waveform: ByteArray): ByteArray {
        var bufferBytes = 0
        val waveBytes = waveform.size
        val stream = ByteArrayOutputStream()
        while (bufferBytes < minimumSize) {
            bufferBytes = bufferBytes + waveBytes
            stream.write(waveform)
        }
        return stream.toByteArray()
    }
}

final class Tones : ToneBufferBuilder(), ToneData {

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

        dialTone = Tone(
            setupSamples(minBufferSize, dialToneData()),
            audioAttributes,
            audioFormat
        )

        misdialTone = Tone(
            setupSamples(minBufferSize, misdialToneData()),
            audioAttributes,
            audioFormat
        )

        engagedTone = Tone(
            setupSamples(minBufferSize, engagedToneData()),
            audioAttributes,
            audioFormat
        )
    }

    public fun finish() {
        stop()
        misdialTone.release()
        dialTone.release()
        engagedTone.release()
    }

    public fun isPlaying(): Boolean = (playing != null || thread != null)

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
