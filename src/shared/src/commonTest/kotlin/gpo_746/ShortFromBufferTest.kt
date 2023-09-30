
package gpo_746

import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class ShortFromBufferTest() {

    private fun assertEqualsHex(expected: String, value: UShort) {
        assertEquals(expected, value.toString(16).uppercase())
    }

    @Test
    public fun shortFromBuffer_gives_a_UShort_of_the_first_2_bytes_in_a_buffer() {
        assertEqualsHex("C0DE", shortFromBuffer(ubyteArrayOf(0xDEu, 0xC0u)))
        assertEqualsHex("CAFE", shortFromBuffer(ubyteArrayOf(0xFEu, 0xCAu)))
        assertEqualsHex("F00D", shortFromBuffer(ubyteArrayOf(0x0Du, 0xF0u)))
    }
}
