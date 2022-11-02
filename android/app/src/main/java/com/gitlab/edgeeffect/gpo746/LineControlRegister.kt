package com.gitlab.edgeeffect.gpo746

enum class LcrBit(val value: Int) {
    ENABLE_RX(0x80),
    ENABLE_TX(0x40),
    MARK_SPACE(0x20),
    PAR_EVEN(0x10),
    ENABLE_PAR(0x08),
    STOP_BITS_2(0x04),
    CS8(0x03),
    CS7(0x02),
    CS6(0x01),
    CS5(0x00)
}

fun lineControlRegister(bits: Set<LcrBit>): Int {
    return bits.fold(0) { lcr: Int, bit: LcrBit -> lcr or bit.value }
}
