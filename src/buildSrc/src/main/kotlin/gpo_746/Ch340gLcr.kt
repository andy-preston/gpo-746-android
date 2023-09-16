enum class Lcr1Bit(val mask: Int) {
    CS5(0x00), // Not defined in FreeBSD, only in NetBSD
    CS6(0x01), // Not defined in FreeBSD, only in NetBSD
    CS7(0x02), // Not defined in FreeBSD, only in NetBSD
    CS8(0x03), // The only one in FreeBSD
    parityEnable(0x08),
    enableTX(0x40),
    enableRX(0x80)
}

enum class Lcr2Bit(val mask: Int) {
    parityNone(0x00),
    parityEven(0x07),  // FreeBSD says 0x07 Linux & NetBSD says 0x10
    parityOdd(0x06),   // FreeBSD says 0x06         NetBSD says 0x00
    parityMark(0x05),  // FreeBSD says 0x05         NetBSD says 0x20
    paritySpace(0x04)  // FreeBSD says 0x04         NetBSD says 0x30
}

final class Ch340gLcr : BytesAndWords() {

    public fun lcr(lowBits: Set<Lcr1Bit>, highBits: Set<Lcr2Bit>): Int {
        return wordFromBytes(
            lowBits.fold(0) { byte, bit -> byte or bit.mask },
            highBits.fold(0) { byte, bit -> byte or bit.mask }
        )
    }

    public fun defaultLcr(): Int {
        return lcr(
            setOf(Lcr1Bit.enableRX, Lcr1Bit.CS8),
            setOf(Lcr2Bit.parityNone)
        )
    }
}
