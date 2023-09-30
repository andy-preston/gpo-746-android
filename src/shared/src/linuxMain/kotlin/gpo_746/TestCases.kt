package gpo_746

class TestCases(driver: Ch340g) {
    private val ch340g = driver
    private var flipBit = false

    private fun serialInput() {
        val string = ch340g.readSerial()
        println(">>>${string}<<<")
    }

    private fun rtsOutput() {
        flipBit = !flipBit
        val state = if (flipBit) "5V" else "Gnd"
        println("RTS ${state}")
        ch340g.writeHandshake(flipBit)
    }

    private fun riInput() {
        val state = if (ch340g.readHandshake()) "5V" else "Gnd"
        println("RI ${state}")
    }

    public fun list(): Map<String, () -> Unit> {
        return mapOf(
            "serial" to ::serialInput,
            "rts" to ::rtsOutput,
            "ri" to ::riInput
        )
    }
}
