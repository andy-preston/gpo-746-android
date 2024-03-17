import java.io.File
import java.io.PrintWriter
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.sin

internal final class Waveform(samplingFrequency: Int = 11025) {

    private val sampleFrequency = samplingFrequency

    public fun getSampleFrequency() = sampleFrequency

    public fun sine(
        waveFrequency: Int,
        indefinite: Boolean
    ): Sequence<Double> {
        @Suppress("MagicNumber")
        val precisionDigits = 5

        @Suppress("MagicNumber")
        val twoPi: Double = 2.0 * PI

        val ratio = sampleFrequency.toDouble() / waveFrequency.toDouble()
        val deltaTheta: Double = twoPi / ratio
        var theta: Double = 0.0
        return sequence {
            while (indefinite || theta <= twoPi) {
                theta = theta + deltaTheta
                yield(
                    sin(theta).toBigDecimal().setScale(
                        precisionDigits,
                        RoundingMode.HALF_EVEN
                    ).toDouble()
                )
                if (indefinite && theta > twoPi) {
                    theta = theta - twoPi
                }
            }
        }
    }

    public fun chopped(
        input: Sequence<Double>,
        chopTenthsSecond: Int
    ): Sequence<Double> {
        @Suppress("MagicNumber")
        val chopSamples = (chopTenthsSecond * sampleFrequency) / 10
        val zeros = generateSequence(0.0) { 0.0 }
        return input.take(chopSamples) + zeros.take(chopSamples)
    }

    public fun modulated(
        input1: Sequence<Double>,
        input2: Sequence<Double>
    ): Sequence<Double> {
        @Suppress("MagicNumber")
        val rogueValue = 10.0
        return input1.zip(input2) { i1, i2 ->
            if (i1 == 0.0 && i2 == 0.0) rogueValue else (i1 + i2) / 2.0
        }.takeWhile {
            it < rogueValue
        }
    }
}

internal final class Tone(w: Waveform) {

    private val waveform = w

    @Suppress("MagicNumber")
    public fun dial(): Sequence<Double> = waveform.modulated(
        waveform.sine(350, true),
        waveform.sine(450, true)
    )

    @Suppress("MagicNumber")
    public fun engaged(): Sequence<Double> = waveform.chopped(
        waveform.sine(400, true),
        4
    )

    @Suppress("MagicNumber")
    public fun misdial(): Sequence<Double> = waveform.sine(400, false)
}

internal final class Scaler {

    private fun integers(
        input: Sequence<Double>,
        scaleFactor: Double
    ): Sequence<Int> = input.map { (it * scaleFactor).toInt() }

    public fun shorts(input: Sequence<Double>): Sequence<Short> = integers(
        input,
        Short.MAX_VALUE.toDouble()
    ).map { it.toShort() }

    public fun bytes(input: Sequence<Double>): Sequence<Byte> = integers(
        input,
        Byte.MAX_VALUE.toDouble()
    ).map { it.toByte() }
}

abstract class ToneGenerator : ConstantsSourceFile() {

    private val waveform = Waveform()
    private val tone = Tone(waveform)
    private val scaler = Scaler()

    private fun arrayOutput(
        name: String,
        tone: Sequence<Double>,
        out: PrintWriter
    ) {
        @Suppress("MagicNumber")
        val indent = " ".repeat(8)
        out.println("    protected val ${name}ToneData = byteArrayOf(")
        out.println(scaler.bytes(tone).joinToString(",\n$indent", indent))
        out.println("    )")
    }

    protected override fun writeFile(constFile: File) {
        var freq = waveform.getSampleFrequency()
        constFile.printWriter().use { out ->
            out.println("package andyp.gpo746\n")
            out.println("const val SAMPLE_FREQUENCY = $freq\n")
            out.println("const val BIT_WIDTH = 8\n")
            out.println("@Suppress(\"MagicNumber\", \"LargeClass\")")
            out.println("abstract class ToneData {\n")
            arrayOutput("dial", tone.dial(), out)
            out.println("")
            arrayOutput("engaged", tone.engaged(), out)
            out.println("")
            arrayOutput("misdial", tone.misdial(), out)
            out.println("}")
        }
    }
}
