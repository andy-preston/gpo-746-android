import java.io.File

@Suppress("MagicNumber")
internal enum class Lcr1Bit(val mask: Int) {
    CS5(0x00), // Not defined in FreeBSD, only in NetBSD
    CS6(0x01), // Not defined in FreeBSD, only in NetBSD
    CS7(0x02), // Not defined in FreeBSD, only in NetBSD
    CS8(0x03), // The only one in FreeBSD
    ParityEnable(0x08),
    EnableTX(0x40),
    EnableRX(0x80)
}

@Suppress("MagicNumber")
internal enum class Lcr2Bit(val mask: Int) {
    ParityNone(0x00),
    ParityEven(0x07), // FreeBSD says 0x07 Linux & NetBSD says 0x10
    ParityOdd(0x06), //  FreeBSD says 0x06         NetBSD says 0x00
    ParityMark(0x05), // FreeBSD says 0x05         NetBSD says 0x20
    ParitySpace(0x04) // FreeBSD says 0x04         NetBSD says 0x30
}

internal final class Ch340gCalculator(baudRate: Ch340gBaudRate) {
    private val ch340gBaudRate = baudRate

    @Suppress("MagicNumber")
    private fun wordFromBytes(highByte: Int, lowByte: Int): Int {
        return lowByte * 256 + highByte
    }

    /* For details of sources, etc, see:
     * src/shared/src/commonMain/kotlin/gpo746/Ch340g.kt
     *
     * There's also a very interesting, but quite long, explanation of baud rate
     * calculation at:
     * https://github.com/nospam2000/ch341-baudrate-calculation
     */
    @Suppress("MagicNumber", "ThrowsCount")
    public fun baudRate(): List<Int> {
        val rate = ch340gBaudRate.checkedRate()
        val clock = ch340gBaudRate.ch340gClock()
        val scalar = ch340gBaudRate.ch340gScaler()
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

abstract class Ch340gConstants : ConstantsSourceFile() {

    @Suppress("MagicNumber")
    protected override fun writeFile(constFile: File) {
        val calculator = Ch340gCalculator(Ch340gBaudRate())
        val (divisorPrescaler, mod) = calculator.baudRate()
        val lcr = calculator.defaultLcr()
        val prefix = "const val CH340G_"
        val suffix = ": UShort ="
        constFile.printWriter().use { out ->
            out.println("package andyp.gpo746\n")
            out.println(
                "${prefix}DIVISOR_PRESCALER$suffix ${divisorPrescaler}u"
            )
            out.println("${prefix}BAUD_MOD$suffix ${mod}u")
            out.println("${prefix}DEFAULT_LCR$suffix ${lcr}u")
        }
    }
}
