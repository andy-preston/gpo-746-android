package com.gitlab.edgeeffect.gpo746

import org.junit.Test
import org.junit.Assert.*

class LcrBitsTest {

    @Test
    fun reduces_a_set_with_one_member_to_the_expected_value() {
        assertSame(0x08, lineControlRegister(setOf(
            LcrBit.ENABLE_PAR
        )))
    }

    @Test
    fun reduces_a_set_with_multiple_members_to_the_expected_value() {
        assertSame(0x46, lineControlRegister(setOf(
            LcrBit.ENABLE_TX,
            LcrBit.STOP_BITS_2,
            LcrBit.CS7
        )))
    }

}
