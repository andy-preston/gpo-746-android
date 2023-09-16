final class Ch340gBaud : BytesAndWords() {
    public fun baudRate(rate: Int): List<Int> {
        val basis = mapOf(
            2400 to listOf(93750, 1),
            4800 to listOf(750000, 2),
            9600 to listOf(750000, 2),
            19200 to listOf(750000, 2),
            38400 to listOf(6000000, 3),
            115200 to listOf(6000000, 3),
        )

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

        // This part comes from the FreeBSD driver...
        // And I find myself asking "why?" of every step and value
        // with no immediate answer forthcoming.
        var mod = 20000000 / rate + 1100
        mod = mod + mod / 2
        mod = (mod + 0xFF) / 0x100

        if (mod < 0 || mod >= 0xFF) {
            throw Exception("Baud rate modulus overflow!");
        }

        return listOf(
            wordFromBytes(scalar, dividend),
            wordFromBytes(mod, 0),
        )
    }

}
