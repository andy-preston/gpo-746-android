final class Ch340Constants {
    private val basis = mapOf(
        2400 to listOf(93750, 1),
        4800 to listOf(750000, 2),
        9600 to listOf(750000, 2),
        19200 to listOf(750000, 2),
        38400 to listOf(6000000, 3),
        115200 to listOf(6000000, 3),
    )

    private fun wordFromBytes(highByte: Int, lowByte: Int): Int {
        return lowByte * 256 + highByte
    }

    public fun baudRate(rate: Int): List<Int> {
        if (!basis.containsKey(rate)) {
            throw Exception("Invalid baud rate ${rate} not in ${basis.keys}")
        }
        val (clock, scalar) = basis[rate]!!
        val remainder = clock % rate
        var dividend = clock / rate
        if (dividend == 0 || dividend >= 0xFF) {
            // In theory, this exception will never be thrown????
            throw Exception("Baud rate divider overflow!")
        }
        if (remainder * 2 >= rate) {
            dividend = dividend + 1
        }
        dividend = 0x0100 - dividend // equivalent of negative 8 bit

        // I found myself asking why of every step and value in this part.
        // This comes from the FreeBSD driver...
        // not that I think rereading it will help that much
        var mod = 20000000 / rate + 1100
        mod = mod + mod / 2
        mod = (mod + 0xFF) / 0x100
        return listOf(
            wordFromBytes(scalar, dividend),
            wordFromBytes(mod, 0),
        )
    }
}
