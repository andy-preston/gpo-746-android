package andyp.gpo746

import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class UsbResultBufferTest : UsbResultBuffer() {

    private fun assertEqualsHex(expected: String, value: UShort) {
        assertEquals(expected, value.toString(16).uppercase())
    }

    private fun aByte(value: Int): Byte {
        return value.toUByte().toByte()
    }

    @Test
    public fun shortFromBuffer_gives_a_UShort_of_the_first_2_bytes_in_a_buffer() {
        assertEqualsHex(
            "C0DE",
            shortFromBuffer(
                byteArrayOf(
                    aByte(0xDE),
                    aByte(0xC0)
                )
            )
        )
        assertEqualsHex(
            "CAFE",
            shortFromBuffer(
                byteArrayOf(
                    aByte(0xFE),
                    aByte(0xCA)
                )
            )
        )
        assertEqualsHex(
            "F00D",
            shortFromBuffer(
                byteArrayOf(
                    aByte(0x0D),
                    aByte(0xF0)
                )
            )
        )
    }

    @Test
    public fun shortFromBuffer_gives_zero_with_a_null_buffer() {
        // See the function definition for a "null rationalization"
        assertEquals(0u, shortFromBuffer(null))
    }
}
