final class AvrConstants {
    private val clockFrequency = 14745600

    // To match both sides of the connection, baudRate should be one of the
    // keys in Ch340gConstants.baudRate.basis
    private val baudRate = 9600

    private val timer1HalfPeriod = 20

    // Better to calculate prescale and get an optimal value
    // (Which, it looks like, I've already done by hand)
    private val timer1Prescale = 256

    public fun map(): Map<String, String>  {
        return mapOf(
            "usartBaudRateRegister" to "${baud()}",
            "timer1ClockSelect" to timer1ClockSelect(),
            "timer1Ticks20ms" to "${timer1Ticks20ms()}"
        )
    }

    private fun baud(): Int {
        // This calculation is actually quite straightforward
        // Especially if you have a look at the one for the CH340G
        // src/buildSrc/src/main/kotlin/gpo_746/Ch340gConstants.kt
        val multiplier: Int = baudRate * 16
        val usartBaudRateRegister: Int = clockFrequency / multiplier
        require(usartBaudRateRegister * multiplier == clockFrequency)
        val derived: Int = clockFrequency / (16 * usartBaudRateRegister)
        require(derived == baudRate)
        return usartBaudRateRegister - 1
    }

    private fun timer1ClockSelect(): String {
        val shiftMap = mapOf(
            0 to "(1 << CS10)",
            8 to "(1 << CS11)",
            64 to "(1 << CS11) | (1 << CS10)",
            256 to "(1 << CS12)",
            1024 to "(1 << CS12) | (1 << CS10)"
        )
        val shifted = shiftMap[timer1Prescale]
        require(shifted != null)
        return shifted
    }

    private fun timer1Ticks(): Int {
        val timerFrequency: Int = clockFrequency / timer1Prescale
        val tick: Double = (1.0 / timerFrequency) * 1000.0
        val ticks: Double = timer1HalfPeriod.toDouble() / tick
        val approxTicks = ticks.toInt()
        require(approxTicks <= 0xffff && approxTicks > 1)
        return approxTicks
    }

}
