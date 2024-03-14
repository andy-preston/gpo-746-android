internal final class Ch340gBaudRate(baud: Int = 9600) {
    private val baudRate = baud

    // Clocks and scalars are used internally by the CH340G to set it's baud
    // rate. The AVR doesn't care about these values. But this is also a list
    // of the available baud rates we can use.
    @Suppress("MagicNumber")
    private val clocksAndScalars = mapOf(
        2400 to listOf(93750, 1),
        4800 to listOf(750000, 2),
        9600 to listOf(750000, 2),
        19200 to listOf(750000, 2),
        38400 to listOf(6000000, 3),
        115200 to listOf(6000000, 3),
    )

    private fun selectedData() = clocksAndScalars[baudRate]!!

    public fun ch340gClock() = selectedData()[0]

    public fun ch340gScaler() = selectedData()[1]

    public fun checkedRate(): Int {
        check(clocksAndScalars.containsKey(baudRate)) {
            "Invalid baud rate $baudRate not in ${clocksAndScalars.keys}"
        }
        return baudRate
    }
}
