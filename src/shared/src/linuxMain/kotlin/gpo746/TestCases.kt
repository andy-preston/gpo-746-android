package andyp.gpo746

class TestCases(driver: Ch340g) {
    private val ch340g = driver
    private var flipBit = false

    private fun highLow(bit: Boolean, line: String): String {
        return if (flipBit) "$line 5V" else "$line Gnd"
    }

    private fun serialInput() {
        val string = ch340g.readSerial()
        println(">>>$string<<<")
    }

    private fun rtsOutput() {
        flipBit = !flipBit
        println(highLow(flipBit, "RTS"))
        ch340g.writeHandshake(flipBit, false)
    }

    private fun dtrOutput() {
        flipBit = !flipBit
        println(highLow(flipBit, "DTR"))
        ch340g.writeHandshake(false, flipBit)
    }

    private fun riInput() {
        println(highLow(ch340g.readHandshake(), "RI"))
    }

    public fun list(): Map<String, () -> Unit> {
        return mapOf(
            "serial" to ::serialInput,
            "rts" to ::rtsOutput,
            "dtr" to ::dtrOutput,
            "ri" to ::riInput
        )
    }
}
