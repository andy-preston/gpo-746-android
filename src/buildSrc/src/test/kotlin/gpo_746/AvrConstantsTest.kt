import kotlin.test.Test
import kotlin.test.assertEquals

// /opt/gradle/gradle-8.3/bin/gradle -p buildSrc test

class AvrConstantsTest {
    // This isn't much in the way of a test
    // But I'm implementing someone else's badly documented
    // algorithm so I haven't got that much of a spec to go on.

    @Test
    fun values_are_what_I_have_already_pre_calculated() {
        val map = AvrConstants().map()
        assertEquals("95", map["baudPrescale"])
        assertEquals("1152", map["timer1Ticks"])
        assertEquals("(1 << CS12)", map["timer1Prescale"])
    }

}
