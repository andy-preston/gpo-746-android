package gpo_746

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import java.io.ByteArrayOutputStream
import java.lang.Thread

abstract class Tone {
    abstract val waveform: ByteArray
    private lateinit var samples: ByteArray
    private lateinit var track: AudioTrack
    private var playing: Boolean = false

    private fun setupSamples() {
        val minimumSize = AudioTrack.getMinBufferSize(
            8000,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_8BIT
        )
        var bufferBytes = 0
        val waveBytes = waveform.size
        val stream = ByteArrayOutputStream()
        while (bufferBytes < minimumSize) {
            bufferBytes = bufferBytes + waveBytes
            stream.write(waveform)
        }
        samples = stream.toByteArray()
    }

    private fun setupAudioTrack() {
        val attributes = AudioAttributes.Builder().setUsage(
            AudioAttributes.USAGE_MEDIA
        ).setContentType(
            AudioAttributes.CONTENT_TYPE_SONIFICATION
        ).build()
        val format = AudioFormat.Builder().setEncoding(
            AudioFormat.ENCODING_PCM_8BIT
        ).setChannelMask(
            AudioFormat.CHANNEL_OUT_MONO
        ).setSampleRate(
            8000
        ).build()
        track = AudioTrack(
            attributes,
            format,
            samples.size,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
    }

    public fun play() {
        playing = true
        Thread({
            var starting: Boolean = true
            try {
                while (playing) {
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
        }).start()
    }

    public fun stop() {
        playing = false;
    }

    public fun isPlaying(): Boolean {
        return playing == true
    }

    public fun start() {
        setupSamples()
        setupAudioTrack()
    }

    public fun finish() {
        track.release()
    }
}
