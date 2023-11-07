               
package gpo_746

import kotlin.test.Test
import kotlin.test.assertTrue


// Mock AudioTrack object
class AudioTrack {
    companion object {
        fun getMinBufferSize(
            sampleRateInHz: Int, 
            channelConfig: Int, 
            audioFormat: Int
        ): Int {
            return 128
        }
    }
}

// Mock AudioFormat object
enum class AudioFormat(val mockValue: Int)  {
    CHANNEL_OUT_MONO(1),
    ENCODING_PCM_8BIT(2)
}

class ToneBufferBuilderTest(): ToneBufferBuilder() {

    val waveform = ByteArrayOf(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5)

    @Test
    public fun buffer_will_exceed_minimum_requirement() {
        assertTrue(setupSamples().size > 128)
    }

    @Test
    public fun buffer_contains_repeated_input_data() {
        setupSamples().chunked(waveform.size).forEach {
            assertSame(waveform, it)
        }
    }
}