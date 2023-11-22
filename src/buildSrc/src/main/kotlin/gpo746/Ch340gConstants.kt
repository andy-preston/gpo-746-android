@Suppress("MagicNumber")
enum class Lcr1Bit(val mask: Int) {
    CS5(0x00), // Not defined in FreeBSD, only in NetBSD
    CS6(0x01), // Not defined in FreeBSD, only in NetBSD
    CS7(0x02), // Not defined in FreeBSD, only in NetBSD
    CS8(0x03), // The only one in FreeBSD
    ParityEnable(0x08),
    EnableTX(0x40),
    EnableRX(0x80)
}

@Suppress("MagicNumber")
enum class Lcr2Bit(val mask: Int) {
    ParityNone(0x00),
    ParityEven(0x07), // FreeBSD says 0x07 Linux & NetBSD says 0x10
    ParityOdd(0x06), //  FreeBSD says 0x06         NetBSD says 0x00
    ParityMark(0x05), // FreeBSD says 0x05         NetBSD says 0x20
    ParitySpace(0x04) // FreeBSD says 0x04         NetBSD says 0x30
}

final class Ch340gConstants {

    @Suppress("MagicNumber")
    public fun map(): Map<String, String> {
        val (divisorPrescale, mod) = baudRate(9600)
        return mapOf(
            "divisorPrescale" to "${divisorPrescale}u",
            "mod" to "${mod}u",
            "defaultLcr" to "${defaultLcr()}u",
            // This is the version used in the prototype hardware
            // Some of the stuff I've seen in BSD drivers wants version >= 0030
            // There are also differences writing handshake with version < 0020
            "usedChipVersion" to "0x0031u"
        )
    }

    @Suppress("MagicNumber")
    private fun wordFromBytes(highByte: Int, lowByte: Int): Int {
        return lowByte * 256 + highByte
    }

    /* For details of sources, etc, see:
     * src/shared/src/commonMain/kotlin/gpo746/Ch340g.kt_template
     *
     * There's also a very interesting, but quite long, explanation of baud rate
     * calculation at:
     * https://github.com/nospam2000/ch341-baudrate-calculation
     */
    @Suppress("MagicNumber", "ThrowsCount")
    public fun baudRate(rate: Int): List<Int> {
        val basis = mapOf(
            2400 to listOf(93750, 1),
            4800 to listOf(750000, 2),
            9600 to listOf(750000, 2),
            19200 to listOf(750000, 2),
            38400 to listOf(6000000, 3),
            115200 to listOf(6000000, 3),
        )

        check(basis.containsKey(rate)) {
            "Invalid baud rate $rate not in ${basis.keys}"
        }

        val (clock, scalar) = basis[rate]!!
        val remainder = clock % rate
        var dividend = clock / rate

        // In theory, this should never go wrong.
        check(dividend > 0 && dividend <= 0xFF) { "Baud rate divider overflow" }

        if (remainder * 2 >= rate) {
            dividend = dividend + 1
        }
        dividend = 0x0100 - dividend // equivalent of negative 8 bit

        // This part comes from the FreeBSD driver...
        // And I find myself asking "why?" of every step and value
        // with no immediate answer forthcoming.
        var mod = 20000000 / rate + 1100
        mod = mod + mod / 2
        mod = (mod + 0xFF) / 0x100

        check(mod >= 0 && mod <= 0xFF) { "Baud rate modulus overflow!" }

        return listOf(
            wordFromBytes(scalar, dividend),
            wordFromBytes(mod, 0),
        )
    }

    public fun lcr(lowBits: Set<Lcr1Bit>, highBits: Set<Lcr2Bit>): Int {
        return wordFromBytes(
            lowBits.fold(0) { byte, bit -> byte or bit.mask },
            highBits.fold(0) { byte, bit -> byte or bit.mask }
        )
    }

    public fun defaultLcr(): Int {
        return lcr(
            setOf(Lcr1Bit.EnableRX, Lcr1Bit.CS8),
            setOf(Lcr2Bit.ParityNone)
        )
    }
}