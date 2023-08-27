abstract class BuildTask: DefaultTask() {
    @Internal
    val cpuClockFrequency = 14745600;

    @TaskAction
    fun build() {
        val dirName = "avr/build"
        val dir = File(dirName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        File("${dirName}/constants.asm").writeText(
            calculate(9600, 20).joinToString("\n")
        )
    }

    fun calculate(baudRate: Int, halfPeriod: Int): List<String> {
        return timer1(halfPeriod.toDouble()) + listOf(baud(baudRate), "")
    }

    fun baud(baudRate: Int): String {
        val multiplier: Int = baudRate * 16
        val prescale: Int = cpuClockFrequency / multiplier
        require(prescale * multiplier == cpuClockFrequency)
        val derived: Int = cpuClockFrequency / (16 * prescale)
        require(derived == baudRate)
        return ".equ baudPrescale = ${prescale - 1}";
    }

    fun timer1(halfPeriod: Double): List<String> {
        val prescaleBits = mapOf(
            0 to "(1 << CS10)",
            8 to "(1 << CS11)",
            64 to "(1 << CS11) | (1 << CS10)",
            256 to "(1 << CS12)",
            1024 to "(1 << CS12) | (1 << CS10)"
        )
        val prescale = 256
        val timerFrequency: Int = cpuClockFrequency / prescale
        val tick: Double = (1.0 / timerFrequency) * 1000.0
        val ringerTicks: Double = halfPeriod / tick
        val approximateTicks: Int = ringerTicks.toInt()
        require(approximateTicks <= 0xffff && approximateTicks > 1);
        return listOf(
            ".equ timer1prescale = ${prescaleBits[prescale]}",
            ".equ ringerTicks = ${approximateTicks}"
        );
    }

}

tasks.register<BuildTask>("build")
