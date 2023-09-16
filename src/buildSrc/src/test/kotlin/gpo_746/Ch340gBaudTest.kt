import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Ch340gBaudTest {
    @Test
    fun invalid_baud_rate_throws_an_exception() {
        val exception = assertFailsWith<Exception>(
            block = {
                Ch340gBaud().baudRate(1024);
            }
        )
        assertEquals(
            "Invalid baud rate 1024 not in [2400, 4800, 9600, 19200, 38400, 115200]",
            exception.message
        )
    }

    @Test
    fun calculated_baud_values_from_free_bsd_match_lookup_table_from_other_drivers() {
        val expectation = mapOf(
            2400 to mapOf("divisorPrescale" to 0xd901, "mod" to 0x0038 ),
            4800 to mapOf("divisorPrescale" to 0x6402, "mod" to 0x001f ),
            9600 to mapOf("divisorPrescale" to 0xb202, "mod" to 0x0013 ),
            19200 to mapOf("divisorPrescale" to 0xd902, "mod" to 0x000d ),
            38400 to mapOf("divisorPrescale" to 0x6403, "mod" to 0x000a ),
            115200 to mapOf("divisorPrescale" to 0xcc03, "mod" to 0x0008 ),
        )
        for ((rate, expected) in expectation) {
            val (divisorPrescale, mod) = Ch340gBaud().baudRate(rate);
            assertEquals(
                expected["divisorPrescale"],
                divisorPrescale,
                "prescale and div matching for ${rate}"
            );
            assertEquals(
                expected["mod"],
                mod,
                "mod matching for ${rate}"
            );
        }
    }
}
