import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class Ch340gLcrTest {

    @Test
    fun bits_are_combined_into_2_bytes_as_big_endian_word() {
        val ch340gLcr = Ch340gLcr()
        assertEquals(
            "83",
            ch340gLcr.defaultLcr().toString(16)
        )
        assertEquals(
            "6ca",
            ch340gLcr.lcr(
                setOf(
                    Lcr1Bit.CS7,
                    Lcr1Bit.parityEnable,
                    Lcr1Bit.enableTX,
                    Lcr1Bit.enableRX
                ),
                setOf(
                    Lcr2Bit.parityOdd
                )
            ).toString(16)
        )
    }

}
