import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.sin

const val TWO_PI: Double = 2.0 * PI
const val PRECISION_DIGITS = 5

final class ToneGeneratorException(message: String) : RuntimeException(message)

abstract class ToneGenerator(sampleFrequency: Int) {

    public val samplingFrequency = sampleFrequency

    private var currentValue: Double = 0.0

    protected abstract fun calculate(): Double

    public fun thisVal(): Double = currentValue

    public open fun next(): Double {
        currentValue = calculate()
        return currentValue
    }
}

abstract class CountingGenerator(
    sampleFrequency: Int
) : ToneGenerator(sampleFrequency) {

    protected var count: Int = 0

    public fun sampleCount() = count

    public override fun next(): Double {
        count = count + 1
        return super.next()
    }
}

final class Sine(
    waveFrequency: Int,
    sampleFrequency: Int
) : ToneGenerator(sampleFrequency) {

    private val ratio = samplingFrequency.toDouble() / waveFrequency.toDouble()

    private val deltaTheta: Double = TWO_PI / ratio

    private var theta: Double = 0.0

    protected override fun calculate(): Double {
        theta = theta + deltaTheta
        while (theta > TWO_PI) {
            theta = theta - TWO_PI
        }
        return sin(theta).toBigDecimal().setScale(
            PRECISION_DIGITS,
            RoundingMode.HALF_EVEN
        ).toDouble()
    }
}

final class Modulator(
    tg1: ToneGenerator,
    tg2: ToneGenerator,
    maximumSampleCount: Int
) : CountingGenerator(tg1.samplingFrequency) {

    private val generator1 = tg1

    private val generator2 = tg2

    private val maxCount = maximumSampleCount

    init {
        if (tg1.samplingFrequency != tg2.samplingFrequency) {
            throw ToneGeneratorException(
                "Sampling frequencies ${tg1.samplingFrequency} != ${tg2.samplingFrequency}"
            )
        }
    }

    public fun bothZeroCrossing(): Boolean {
        return generator1.thisVal() == 0.0 && generator2.thisVal() == 0.0
    }

    protected override fun calculate(): Double {
        if (count > maxCount) {
            throw ToneGeneratorException(
                "$maxCount samples generated without zero crossing"
            )
        }
        return (generator1.next() + generator2.next()) / 2.0
    }
}

final class ToneScaler(tg: ToneGenerator) {

    private val generator = tg

    private val scaleFactor = Int.MAX_VALUE.toDouble()

    public fun next(): Int {
        return (generator.next() * scaleFactor).toInt()
    }
}

final class Tones {

    @Suppress("MagicNumber")
    public fun dial(): IntArray {
        val generator1 = Sine(350, 11025)
        val generator2 = Sine(450, 11025)
        val modulator = Modulator(generator1, generator2, 512)
        val scaler = ToneScaler(modulator)
        val values = ArrayList<Int>()
        do {
            values.add(scaler.next())
        } while (!modulator.bothZeroCrossing())
        return values.toIntArray()
    }
}
