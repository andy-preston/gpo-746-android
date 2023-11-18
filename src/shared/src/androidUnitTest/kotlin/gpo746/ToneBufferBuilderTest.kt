package andyp.gpo746

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ToneBufferBuilderTest : ToneBufferBuilder() {

    private val waveform = byteArrayOf(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5)

    @Test
    public fun buffer_will_exceed_minimum_requirement() {
        assertTrue(setupSamples(waveform, 128).size >= 128)
    }

    @Test
    public fun buffer_contains_repeated_input_data() {
        var pos = 0
        setupSamples(waveform, 64).forEach {
            assertSame(waveform[pos], it)
            pos = if (pos < waveform.size - 1) pos + 1 else 0
        }
    }
}
