import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

final class ToneGeneratorTest {

    @Test
    fun the_period_of_the_wave_is_a_ratio_of_the_wave_frequency_to_the_sample_frequency() {
        /* In the past, if I've done any signal processing, I've been happy
         * enough to plot it on a graph and be able to say "yeah, that looks
         * about right". But, using TDD, you need an arithmetic proof and that's
         * been (shall we say) "interesting" pinning down the the required
         * precision.
         *
         * 96 / 6 = 16 and half of 16 is 8
         */
        val waveform = Waveform(96)
        var cycle = 0
        waveform.sine(6, true).take(5 * 16).chunked(16).forEach { chunk ->
            cycle = cycle + 1
            for (sample in 0..7) {
                val next = chunk[sample]
                assertTrue(next >= 0.0, "$cycle/$sample - $next isn't positive")
            }
            for (sample in 8..15) {
                val next = chunk[sample]
                assertTrue(next <= 0.0, "$cycle/$sample - $next isn't negative")
            }
        }
    }

    @Test
    fun the_sine_wave_does_not_peak() {
        val waveform = Waveform(200)
        waveform.sine(12, true).take(200).forEachIndexed { sample, next ->
            assertTrue(next <= 1.0, "Sine $sample - $next > 1.0")
            assertTrue(next >= -1.0, "Sine $sample - $next < -1.0")
        }
    }

    @Test
    fun the_sine_wave_can_be_scaled_to_shorts_by_the_16_bit_tone_scaler() {
        val waveform = Waveform(200)
        val scaler = Scaler()
        val positiveMax = Short.MAX_VALUE
        val negativeMax = -positiveMax
        // This headroom value isn't great but it's not awful either.
        val goalHeadroom = 4231000
        val positiveGoal = positiveMax - goalHeadroom
        val negativeGoal = negativeMax + goalHeadroom
        var positivePeak: Short = 0
        var negativePeak: Short = 0
        scaler.shorts(
            waveform.sine(12, true)
        ).take(200).forEachIndexed { sample, next ->
            if (next > positivePeak) {
                positivePeak = next
            }
            if (next < negativePeak) {
                negativePeak = next
            }
            assertTrue(
                next <= positiveMax,
                "Scaled sine $sample - $next > $positiveMax"
            )
            assertTrue(
                next >= negativeMax,
                "Scaled sine $sample - $next < $negativeMax"
            )
        }
        assertTrue(
            positivePeak > positiveGoal,
            "Scaled sine positive peak $positivePeak < $positiveGoal"
        )
        assertTrue(
            negativePeak < negativeGoal,
            "Scaled sine negative peak $negativePeak > $negativeGoal"
        )
    }

    @Test
    fun the_modulator_does_not_peak_but_approaches_the_peaks() {
        var positivePeak = 0.0
        var negativePeak = 0.0
        val waveform = Waveform(200)
        waveform.modulated(
            waveform.sine(6, true),
            waveform.sine(8, true)
        ).take(200).forEachIndexed { sample, next ->
            if (next > positivePeak) {
                positivePeak = next
            }
            if (next < negativePeak) {
                negativePeak = next
            }
            assertTrue(next <= 1.0, "Modulator $sample - $next > 1.0")
            assertTrue(next >= -1.0, "Modulator $sample - $next < -1.0")
        }
        assertTrue(positivePeak > 0.7, "Modulator peak $positivePeak < 0.9")
        assertTrue(negativePeak < -0.7, "Modulator peak $negativePeak > -0.9")
    }

    @Test
    fun the_modulator_is_scaled_to_bytes_by_the_byte_scaler() {
        val waveform = Waveform(200)
        val scaler = Scaler()
        val positiveMax = Byte.MAX_VALUE
        val negativeMax = -positiveMax
        // This headroom value looks even less great than the sine one
        val goalHeadroom = 28
        val positiveGoal = positiveMax - goalHeadroom
        val negativeGoal = negativeMax + goalHeadroom
        var positivePeak: Byte = 0
        var negativePeak: Byte = 0
        scaler.bytes(
            waveform.modulated(waveform.sine(6, true), waveform.sine(8, true))
        ).take(200).forEachIndexed { sample, next ->
            if (next > positivePeak) {
                positivePeak = next
            }
            if (next < negativePeak) {
                negativePeak = next
            }
            assertTrue(
                next <= positiveMax,
                "Scaled modulator $sample - $next > $positiveMax"
            )
            assertTrue(
                next >= negativeMax,
                "Scaled modulator $sample - $next < $negativeMax"
            )
        }
        assertTrue(
            positivePeak >= positiveGoal,
            "Scaled modulator positive peak $positivePeak < $positiveGoal"
        )
        assertTrue(
            negativePeak <= negativeGoal,
            "Scaled modulator negative peak $negativePeak > $negativeGoal"
        )
    }

    @Test
    public fun chopper_chops_in_specified_period_and_records_cycles() {
        val mockGenerator = generateSequence(1.0) { 1.0 }
        val waveform = Waveform(20)
        val chopped = waveform.chopped(mockGenerator, 3)
        chopped.chunked(12).forEach { chunk ->
            for (sample in 0..5) {
                assertEquals(1.0, chunk[sample], "first period sample $sample not 1")
            }
            for (sample in 6..11) {
                assertEquals(0.0, chunk[sample], "second period sample $sample not 0")
            }
        }
        assertEquals(12, chopped.count(), "chopped should have 12 elements")
    }
}
