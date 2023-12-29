package andyp.gpo746

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import java.io.ByteArrayOutputStream
import java.lang.Thread

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

const val SAMPLE_RATE = 8000 // Hz

final class Tones : ToneBufferBuilder() {
    private var playing: AudioTrack? = null
    private var stopped: Boolean = true
    private val dialSamples: ByteArray
    private val misdialSamples: ByteArray
    private val dialTrack: AudioTrack
    private val misdialTrack: AudioTrack

    init {
        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_8BIT
        )
        val toneSamples = ToneSamples()
        dialSamples = setupSamples(minBufferSize, toneSamples.dial)
        misdialSamples = setupSamples(minBufferSize, toneSamples.misdial)
        dialTrack = audioTrack(dialSamples.size)
        misdialTrack = audioTrack(misdialSamples.size)
    }

    private fun audioTrack(samplesSize: Int) = AudioTrack(
        AudioAttributes.Builder().setUsage(
            AudioAttributes.USAGE_MEDIA
        ).setContentType(
            AudioAttributes.CONTENT_TYPE_SONIFICATION
        ).build(),
        AudioFormat.Builder().setEncoding(
            AudioFormat.ENCODING_PCM_8BIT
        ).setChannelMask(
            AudioFormat.CHANNEL_OUT_MONO
        ).setSampleRate(
            SAMPLE_RATE
        ).build(),
        samplesSize,
        AudioTrack.MODE_STREAM,
        AudioManager.AUDIO_SESSION_ID_GENERATE
    )

    public fun finish() {
        stop()
        misdialTrack.release()
        dialTrack.release()
    }

    public fun stop() {
        while (!stopped) { playing = null }
    }

    public fun play(misdial: Boolean) {
        val track = if (misdial) misdialTrack else dialTrack
        if (playing == track) {
            return
        }
        val samples = if (misdial) misdialSamples else dialSamples
        stop()
        Thread({
            var starting: Boolean = true
            playing = track
            stopped = false
            try {
                while (playing != null) {
                    track.write(samples, 0, samples.size)
                    if (starting) {
                        track.play()
                        starting = false
                    }
                }
            } catch (e: Exception) {
                Log.e("gpo746", e.toString())
            } catch (e: OutOfMemoryError) {
                Log.e("gpo746", e.toString())
            }
            track.stop()
            stopped = true
        }).start()
    }
}
