import kotlin.test.Test
import kotlin.test.assertEquals

class AvrConstantsTest {
    // This isn't much in the way of a test but it does
    // check the values I'm currently using are as I would expect them to be.

    @Test
    fun values_are_what_I_have_already_pre_calculated() {
        val generator = AvrConstantsGenerator()
        assertEquals(95, generator.baud())
        assertEquals(1152, generator.timer1Ticks(RING_HALF_PERIOD_MILLISECONDS))
        assertEquals(1728, generator.timer1Ticks(DEBOUNCE_PERIOD_MILLISECONDS))
        assertEquals("(1 << CS12)", generator.timer1ClockSelect())
    }
}
