import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

final class MockGenerator(
    sampleFrequency: Int
) : ToneGenerator(sampleFrequency) {

    protected override fun calculate(): Double {
        return 1.0
    }
}

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
        val sine = Sine(6, 96)
        for (cycle in 1..5) {
            for (sample in 1..8) {
                val next = sine.next()
                assertTrue(next >= 0.0, "$cycle/$sample - $next isn't positive")
            }
            for (sample in 9..16) {
                val next = sine.next()
                assertTrue(next <= 0.0, "$cycle/$sample - $next isn't negative")
            }
        }
    }

    @Test
    fun the_sine_wave_does_not_peak() {
        val sine = Sine(12, 200)
        for (sample in 1..200) {
            val next = sine.next()
            assertTrue(next <= 1.0, "Sine $sample - $next > 1.0")
            assertTrue(next >= -1.0, "Sine $sample - $next < -1.0")
        }
    }

    @Test
    fun the_sine_wave_is_scaled_to_integers_by_the_tone_scaler() {
        val sine = Sine(12, 200)
        val scaler = ToneScaler(sine)
        val positiveMax = Int.MAX_VALUE
        val negativeMax = -positiveMax
        // This headroom value isn't great but it's not awful either.
        val goalHeadroom = 4231000
        val positiveGoal = positiveMax - goalHeadroom
        val negativeGoal = negativeMax + goalHeadroom
        var positivePeak = 0
        var negativePeak = 0
        for (sample in 1..200) {
            val next = scaler.next()
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
    fun the_modulator_throws_if_the_supplied_waves_have_different_sampling_frequencies() {
        val sine1 = Sine(6, 96)
        val sine2 = Sine(8, 42)
        assertFailsWith<ToneGeneratorException> {
            Modulator(sine1, sine2)
        }
    }

    @Test
    fun the_modulator_keeps_track_of_how_many_samples_have_passed_through() {
        val modulator = Modulator(
            Sine(6, 96),
            Sine(8, 96)
        )
        do {
            modulator.next()
        } while (!modulator.bothZeroCrossing())
        assertTrue(modulator.sampleCount() <= 24)
    }

    @Test
    fun the_modulator_does_not_peak_but_approaches_the_peaks() {
        val modulator = Modulator(
            Sine(6, 200),
            Sine(8, 200)
        )
        var positivePeak = 0.0
        var negativePeak = 0.0
        for (sample in 1..200) {
            val next = modulator.next()
            if (next > positivePeak) {
                positivePeak = next
            }
            if (next < negativePeak) {
                negativePeak = next
            }
            assertTrue(next <= 1.0, "Modulator $sample - $next > 1.0")
            assertTrue(next >= -1.0, "Modulator $sample - $next < -1.0")
        }
        assertTrue(positivePeak > 0.9, "Modulator peak $positivePeak < 0.9")
        assertTrue(negativePeak < -0.9, "Modulator peak $negativePeak > -0.9")
    }

    @Test
    fun the_modulator_is_scaled_to_integers_by_the_tone_scaler() {
        val modulator = Modulator(
            Sine(6, 200),
            Sine(8, 200)
        )
        val scaler = ToneScaler(modulator)
        val positiveMax = Int.MAX_VALUE
        val negativeMax = -positiveMax
        // This headroom value looks even less great than the sine one
        val goalHeadroom = 52760000
        val positiveGoal = positiveMax - goalHeadroom
        val negativeGoal = negativeMax + goalHeadroom
        var positivePeak = 0
        var negativePeak = 0
        for (sample in 1..200) {
            val next = scaler.next()
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
            positivePeak > positiveGoal,
            "Scaled modulator positive peak $positivePeak < $positiveGoal"
        )
        assertTrue(
            negativePeak < negativeGoal,
            "Scaled modulator negative peak $negativePeak > $negativeGoal"
        )
    }

    @Test
    public fun chopper_chops_in_specified_period_and_records_cycles() {
        val generator = MockGenerator(20)
        val chopper = Chopper(generator, 3)

        for (sample in 1..6) {
            assertEquals(1.0, chopper.next(), "first period sample $sample not 1")
        }
        assertEquals(0, chopper.cyclesCompleted(), "chop cycle should not have completed")
        for (sample in 1..6) {
            assertEquals(0.0, chopper.next(), "second period sample $sample not 0")
        }
        assertEquals(1, chopper.cyclesCompleted(), "chop cycle should have completed")
        assertEquals(1.0, chopper.next(), "final sample not 1")
    }
}
