package gpo_746

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import java.io.ByteArrayOutputStream

final class Tones {

    val dial_tone_8000Hz_8bit_mono = byteArrayOf(
        // I really should do a Fourier analysis of this and work out what it's
        // made of. I started with a 32bit float 44.1 recording and cut it down,
        // dropped the word-length and down-sampled it checking it still sounded
        // right and it's still fine.
          4,   43,   55,   67,   76,   67,   60,   38,   24,    2,
        -17,  -33,  -36,  -41,  -31,  -22,  -10,
          7,   16,   28,   36,   31,   29,   22,    1,
         -8,  -22,  -30,  -34,  -33,  -21,  -20,   -9,
         39,   38,   51,   72,   67,   66,   70,   36,
        // This little `-9, 0,` "blip" offended my sense of symmetry and
        // I tried to cut it out and it drastically altered the timbre.
        // I really should do a Fourier analysis.
         -9,    0,
        -46,  -65,  -83,  -93,  -79,  -83,  -32,  -13,
         20,   59,   64,  116,   98,  111,   93,   80,   60,
        -16,   -6,  -78,  -69,  -90, -118,  -83,  -84,  -42,  -26,
         17,   47,   49,   91,   93,   88,   92,   66,   35,   20,
        -10,  -54,  -48,  -67,  -61,  -56,  -46,  -19,  -8
    )

    private lateinit var track: AudioTrack
    private lateinit var bytes: ByteArray

    public fun samples(): ByteArray {
        if (!this::bytes.isInitialized) {
            val minimumSize = AudioTrack.getMinBufferSize(
                8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT
            )
            var bufferBytes = 0
            val waveBytes = dial_tone_8000Hz_8bit_mono.size
            val stream = ByteArrayOutputStream()
            while (bufferBytes < minimumSize) {
                bufferBytes = bufferBytes + waveBytes
                stream.write(dial_tone_8000Hz_8bit_mono)
            }
            bytes = stream.toByteArray()
        }
        return bytes
    }

    public fun audioTrack(): AudioTrack {
        if (!this::track.isInitialized) {
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
                bytes.size,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
        }
        return track
    }

    public fun close() {
        track.release()
    }
}
