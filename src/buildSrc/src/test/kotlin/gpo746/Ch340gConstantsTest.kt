import kotlin.test.Test
import kotlin.test.assertEquals

class Ch340gConstantsTest {

    @Test
    fun bits_are_combined_into_2_bytes_as_big_endian_word() {
        val calculator = Ch340gCalculator(Ch340gBaudRate())
        assertEquals(
            "83",
            calculator.defaultLcr().toString(16)
        )
        assertEquals(
            "6ca",
            calculator.lcr(
                setOf(
                    Lcr1Bit.CS7,
                    Lcr1Bit.ParityEnable,
                    Lcr1Bit.EnableTX,
                    Lcr1Bit.EnableRX
                ),
                setOf(
                    Lcr2Bit.ParityOdd
                )
            ).toString(16)
        )
    }

    @Test
    fun calculated_baud_values_from_free_bsd_match_lookup_table_from_other_drivers() {
        val expectation = mapOf(
            2400 to mapOf("divisorPreScale" to 0xd901, "mod" to 0x0038),
            4800 to mapOf("divisorPreScale" to 0x6402, "mod" to 0x001f),
            9600 to mapOf("divisorPreScale" to 0xb202, "mod" to 0x0013),
            19200 to mapOf("divisorPreScale" to 0xd902, "mod" to 0x000d),
            38400 to mapOf("divisorPreScale" to 0x6403, "mod" to 0x000a),
            115200 to mapOf("divisorPreScale" to 0xcc03, "mod" to 0x0008),
        )
        for ((rate, expected) in expectation) {
            val (divisorPreScale, mod) = Ch340gCalculator(
                Ch340gBaudRate(rate)
            ).baudRate()
            assertEquals(
                expected["divisorPreScale"],
                divisorPreScale,
                "prescale and div matching for $rate"
            )
            assertEquals(
                expected["mod"],
                mod,
                "mod matching for $rate"
            )
        }
    }
}
