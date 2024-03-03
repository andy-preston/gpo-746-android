import java.io.File
import java.io.PrintWriter

abstract class Scaler<T> {
    abstract protected val scaleFactor: Double
    protected fun toInt(double: Double): Int = (double * scaleFactor).toInt()
    abstract public fun scale(double: Double): T
    abstract public fun width(): Int
    abstract public fun type(): String
}

final class ShortScaler: Scaler<Short>() {
    override protected val scaleFactor = Short.MAX_VALUE.toDouble()
    override public fun scale(double: Double): Short = super.toInt(double).toShort()
    override public fun width(): Int = Short.SIZE_BITS
    override public fun type(): String = "short"
}

final class ByteScaler: Scaler<Byte>() {
    override protected val scaleFactor = Byte.MAX_VALUE.toDouble()
    override public fun scale(double: Double): Byte = super.toInt(double).toByte()
    override public fun width(): Int = Byte.SIZE_BITS
    override public fun type(): String = "byte"
}

@Suppress("MagicNumber")
final class Tones {

    private val samplingFrequency = 11025

    private val scaler: Scaler<Byte> = ByteScaler()

    private fun dial(): Sequence<Double> {
        val modulator = Modulator(
            Sine(350, samplingFrequency),
            Sine(450, samplingFrequency)
        )
        return sequence {
            while (!modulator.bothZeroCrossing()) {
                yield(modulator.next())
            }
        }
    }

    private fun engaged(): Sequence<Double> {
        val chopper = Chopper(
            Sine(400, samplingFrequency),
            4
        )
        return sequence {
            while (chopper.cyclesCompleted() < 1) {
                yield(chopper.next())
            }
        }
    }

    private fun misdial(): Sequence<Double> {
        val sine = Sine(400, samplingFrequency)
        return sequence {
            while (sine.cyclesCompleted() < 1) {
                yield(sine.next())
            }
        }
    }

    private fun arrayOutput(
        name: String,
        tone: Sequence<Double>,
        out: PrintWriter
    ) {
        val indent = " ".repeat(8)
        out.println("    protected val ${name}ToneData = ${scaler.type()}ArrayOf(")
        out.println(tone.map { scaler.scale(it) }.joinToString(",\n$indent", indent))
        out.println("    )")
    }

    public fun fileOutput(sourceFile: File) {
        sourceFile.printWriter().use { out ->
            out.println("package andyp.gpo746\n")
            out.println("const val SAMPLE_RATE = $samplingFrequency\n")
            out.println("const val BIT_WIDTH = ${scaler.width()}\n")
            out.println("@Suppress(\"MagicNumber\", \"LargeClass\")")
            out.println("abstract class ToneData {\n")
            arrayOutput("dial", dial(), out)
            out.println("")
            arrayOutput("engaged", engaged(), out)
            out.println("")
            arrayOutput("misdial", misdial(), out)
            out.println("}")
        }
    }
}
