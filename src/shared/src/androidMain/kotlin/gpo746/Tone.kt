package andyp.gpo746

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import java.io.ByteArrayOutputStream

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

final class Tone(minBufferSize: Int, waveform: ByteArray) : ToneBufferBuilder() {
    val samples = setupSamples(minBufferSize, waveform)
    val track = AudioTrack(
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
        samples.size,
        AudioTrack.MODE_STREAM,
        AudioManager.AUDIO_SESSION_ID_GENERATE
    )

    public fun release() {
        track.release()
    }

    public fun write() {
        track.write(samples, 0, samples.size)
    }

    public fun start() {
        track.play()
    }

    public fun stop() {
        track.stop()
    }
}
