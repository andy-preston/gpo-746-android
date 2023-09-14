import kotlin.test.Test
import kotlin.test.assertEquals

class AvrConstantsTest {
    // This isn't much in the way of a test but it does
    // check the values I'm currently using are as I would expect them to be.

    @Test
    fun values_are_what_I_have_already_pre_calculated() {
        val map = AvrConstants().map()
        assertEquals("95", map["baudPrescale"])
        assertEquals("1152", map["timer1Ticks"])
        assertEquals("(1 << CS12)", map["timer1Prescale"])
    }

}
