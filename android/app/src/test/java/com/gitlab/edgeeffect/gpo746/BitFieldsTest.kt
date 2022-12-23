package com.gitlab.edgeeffect.gpo746

import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertEquals

class BitFieldsTest {

    @Test
    fun lcr_reduces_a_set_with_one_member_to_the_expected_value() {
        assertSame(0x08, LcrBit.asByte(setOf(
            LcrBit.ENABLE_PAR
        )))
    }

    @Test
    fun lcr_reduces_a_set_with_multiple_members_to_the_expected_value() {
        assertSame(0x46, LcrBit.asByte(setOf(
            LcrBit.ENABLE_TX,
            LcrBit.STOP_BITS_2,
            LcrBit.CS7
        )))
    }

    @Test
    fun modem_reduces_an_empty_set() {
        assertEquals(0x00, ModemBit.asByte(setOf<ModemBit>()))
    }

    @Test
    fun modem_reduces_a_single_value() {
        assertEquals(0x20, ModemBit.asByte(setOf(
            ModemBit.DTR
        )))
    }

    @Test
    fun modem_reduces_a_set() {
        assertEquals(0x60, ModemBit.asByte(setOf(
            ModemBit.DTR,
            ModemBit.RTS
        )))
    }

    @Test
    fun gcl_expands_a_value_to_a_set() {
        assertEquals(setOf(
            GclBit.CTS,
            GclBit.RI
        ), GclBit.fromByte(0x05.toByte()))
    }

    @Test
    fun gcl_expands_an_RI_signal_correctly() {
        assertEquals(setOf(
            GclBit.RI
        ), GclBit.fromByte(0x04.toByte()))
    }

    @Test
    fun gcl_expands_zero_to_an_empty_set() {
        assertEquals(setOf<GclBit>(), GclBit.fromByte(0x00.toByte()))
    }

}
