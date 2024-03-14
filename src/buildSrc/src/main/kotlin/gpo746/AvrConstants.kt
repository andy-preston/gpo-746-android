import java.io.File

// This is a "universal, perfect for serial" crystal frequency that's easily
// divisible by any of our available baud rates.
private const val CLOCK_FREQUENCY = 14745600

internal const val RING_HALF_PERIOD_MILLISECONDS = 20
internal const val DEBOUNCE_PERIOD_MILLISECONDS = 30

// Any of the available prescalers are perfectly valid
// Except 1 - which will have a number of ticks that will overflow the 16 bit
// counter. The 8 bit counter is unsuitable for the intervals we need as none
// of the prescalers are enough to prevent an 8-bit overflow.
private const val TIMER1_PRE_SCALE = 256

internal final class AvrConstantsGenerator {
    @Suppress("MagicNumber")
    public fun baud(): Int {
        val baudRate = Ch340gBaudRate().checkedRate()
        // This calculation is actually quite straightforward
        // Especially if you have a look at the one for the CH340G
        // src/buildSrc/src/main/kotlin/gpo746/Ch340gConstants.kt
        val multiplier: Int = baudRate * 16
        val usartBaudRateRegister: Int = CLOCK_FREQUENCY / multiplier
        require(usartBaudRateRegister * multiplier == CLOCK_FREQUENCY)
        val derived: Int = CLOCK_FREQUENCY / (16 * usartBaudRateRegister)
        require(derived == baudRate)
        return usartBaudRateRegister - 1
    }

    @Suppress("MagicNumber")
    public fun timer1ClockSelect(): String {
        val shiftMap = mapOf(
            0 to "0",
            1 to "(1 << CS10)",
            8 to "(1 << CS11)",
            64 to "(1 << CS11) | (1 << CS10)",
            256 to "(1 << CS12)",
            1024 to "(1 << CS12) | (1 << CS10)"
        )
        val shifted = shiftMap[TIMER1_PRE_SCALE]
        // I don't see how this value can ever be null?
        // But a quick require at compile-time doesn't hurt.
        require(shifted != null)
        return shifted
    }

    @Suppress("MagicNumber")
    public fun timer1Ticks(milliseconds: Int): Int {
        val timerFrequency: Int = CLOCK_FREQUENCY / TIMER1_PRE_SCALE
        val tick: Double = (1.0 / timerFrequency) * 1000.0
        val ticks: Double = milliseconds.toDouble() / tick
        val roundedTicks = ticks.toInt()
        require(roundedTicks <= 0xffff && roundedTicks > 0)
        return roundedTicks
    }
}

final class AvrConstants {
    public fun fileOutput(constFile: File) {
        val generator = AvrConstantsGenerator()
        val baud = generator.baud()
        val cs = generator.timer1ClockSelect()
        val ticksRing = generator.timer1Ticks(RING_HALF_PERIOD_MILLISECONDS)
        val ticksDebounce = generator.timer1Ticks(DEBOUNCE_PERIOD_MILLISECONDS)
        constFile.printWriter().use { out ->
            out.println("    .equ usart_baud_rate_register = $baud")
            out.println("    .equ timer1_clock_select = $cs")
            out.println("    .equ timer1_ring_ticks = $ticksRing")
            out.println("    .equ timer1_debounce_ticks = $ticksDebounce")
        }
    }
}
