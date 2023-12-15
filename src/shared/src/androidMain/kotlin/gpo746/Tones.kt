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
    private var playing: Boolean = false
    private var stopped: Boolean = true

    private val minBufferSize = AudioTrack.getMinBufferSize(
        SAMPLE_RATE,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_8BIT
    )

    @Suppress("MagicNumber")
    private val dialSamples = setupSamples(
        minBufferSize,
        byteArrayOf(
            /* I really should do a Fourier analysis of this and work out
             * what it's made of. I started with a 32bit float 44.1
             * recording and cut it down, dropped the word-length and
             * down-sampled it checking it still sounded right and it's
             * still fine.
             */
            4, 43, 55, 67, 76, 67, 60, 38, 24, 2,
            -17, -33, -36, -41, -31, -22, -10,
            7, 16, 28, 36, 31, 29, 22, 1,
            -8, -22, -30, -34, -33, -21, -20, -9,
            39, 38, 51, 72, 67, 66, 70, 36,
            /* This little `-9, 0,` "blip" offended my sense of symmetry and
             * I tried to cut it out and it drastically altered the timbre.
             * I really should do a Fourier analysis.
             */
            -9, 0,
            -46, -65, -83, -93, -79, -83, -32, -13,
            20, 59, 64, 116, 98, 111, 93, 80, 60,
            -16, -6, -78, -69, -90, -118, -83, -84, -42, -26,
            17, 47, 49, 91, 93, 88, 92, 66, 35, 20,
            -10, -54, -48, -67, -61, -56, -46, -19, -8
        )
    )

    @Suppress("MagicNumber")
    private val misdialSamples = setupSamples(
        minBufferSize,
        byteArrayOf(
            0, -39, -68, -108, -108, -108, -82, -52, -16, -78, -12,
            44, 60, 96, 96, 96, 80, 53, 73, 77,
            -26, -39, -88, -108, -111, -98, -78, -32, -32, -78,
            21, 44, 73, 96, 100, 93, 73, 50, 90, 47,
            -39, -49, -105, -105, -115, -92, -72, -16, -59, -52,
            37, 47, 86, 96, 100, 86, 63, 53, 96, 11,
        )
    )

    private val dialTrack = audioTrack(dialSamples.size)

    private val misdialTrack = audioTrack(misdialSamples.size)

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

    public fun stop() {
        while (!stopped) { playing = false }
    }

    public fun finish() {
        stop()
        misdialTrack.release()
        dialTrack.release()
    }

    private fun play(track: AudioTrack, samples: ByteArray) {
        stop()
        Thread({
            var starting: Boolean = true
            playing = true
            stopped = false
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
            stopped = true
        }).start()
    }

    public fun playing(): Boolean = playing || !stopped

    public fun playDialTone() = play(dialTrack, dialSamples)

    public fun playMisdialTone() = play(misdialTrack, misdialSamples)
}
