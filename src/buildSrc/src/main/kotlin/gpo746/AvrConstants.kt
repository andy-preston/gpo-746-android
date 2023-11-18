private const val CLOCK_FREQUENCY = 14745600

private const val BAUD_RATE = 9600

private const val RING_HALF_PERIOD_MILLISECONDS = 20

private const val TIMER1_PRE_SCALE = 256

final class AvrConstants {
    public fun map(): Map<String, String> {
        return mapOf(
            "usart_baud_rate_register" to "${baud()}",
            "timer1_clock_select" to timer1ClockSelect(),
            "timer1_20ms_ticks" to "${timer1Ticks20ms()}"
        )
    }

    @Suppress("MagicNumber")
    private fun baud(): Int {
        // This calculation is actually quite straightforward
        // Especially if you have a look at the one for the CH340G
        // src/buildSrc/src/main/kotlin/gpo746/Ch340gConstants.kt
        val multiplier: Int = BAUD_RATE * 16
        val usartBaudRateRegister: Int = CLOCK_FREQUENCY / multiplier
        require(usartBaudRateRegister * multiplier == CLOCK_FREQUENCY)
        val derived: Int = CLOCK_FREQUENCY / (16 * usartBaudRateRegister)
        require(derived == BAUD_RATE)
        return usartBaudRateRegister - 1
    }

    @Suppress("MagicNumber")
    private fun timer1ClockSelect(): String {
        val shiftMap = mapOf(
            0 to "(1 << CS10)",
            8 to "(1 << CS11)",
            64 to "(1 << CS11) | (1 << CS10)",
            256 to "(1 << CS12)",
            1024 to "(1 << CS12) | (1 << CS10)"
        )
        val shifted = shiftMap[TIMER1_PRE_SCALE]
        require(shifted != null)
        return shifted
    }

    @Suppress("MagicNumber")
    private fun timer1Ticks20ms(): Int {
        val timerFrequency: Int = CLOCK_FREQUENCY / TIMER1_PRE_SCALE
        val tick: Double = (1.0 / timerFrequency) * 1000.0
        val ticks: Double = RING_HALF_PERIOD_MILLISECONDS.toDouble() / tick
        val approxTicks = ticks.toInt()
        require(approxTicks <= 0xffff && approxTicks > 1)
        return approxTicks
    }
}
