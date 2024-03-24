package andyp.gpo746

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import java.io.ByteArrayOutputStream
import java.lang.Thread

enum class ToneSelection { DIAL, MISDIAL, ENGAGED }

abstract class ToneBufferFiller {

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

final class Tones : ToneSamples() {

    private var thread: Thread? = null
    private var sampleSource: ByteArray? = null
    private val buffer: ByteArray
    private val audioTrack: AudioTrack

    init {
        val bufferSize = AudioTrack.getMinBufferSize(
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

        buffer = ByteArray(bufferSize)

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
                write()
                audioTrack.play()
                while (sampleSource != null) {
                    write()
                }
                audioTrack.stop()
            }
        ).start()
    }
    private fun write() {
        audioTrack.write(buffer, 0, buffer.size)
    }
}
