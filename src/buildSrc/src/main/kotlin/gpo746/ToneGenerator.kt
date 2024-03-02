import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.sin

const val TWO_PI: Double = 2.0 * PI
const val PRECISION_DIGITS = 5

final class ToneGeneratorException(message: String) : RuntimeException(message)

interface ToneGenerator {
    public fun next(): Double
}

final class Sine(
    waveFrequency: Int,
    samplingFrequency: Int
) : ToneGenerator {
    private val ratio = samplingFrequency.toDouble() / waveFrequency.toDouble()
    private val deltaTheta: Double = TWO_PI / ratio
    private var theta: Double = 0.0

    public override fun next(): Double {
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
    frequency1: Int,
    frequency2: Int,
    samplingFrequency: Int,
    maximumSampleCount: Int
) : ToneGenerator {
    private val sine1 = Sine(frequency1, samplingFrequency)
    private val sine2 = Sine(frequency2, samplingFrequency)
    private val maxCount = maximumSampleCount

    private var val1: Double = 0.0
    private var val2: Double = 0.0
    private var sampleCount: Int = 0

    public fun bothZeroCrossing(): Boolean {
        return val1 == 0.0 && val2 == 0.0
    }

    public fun samplesAccumulated(): Int {
        return sampleCount
    }

    public override fun next(): Double {
        sampleCount = sampleCount + 1
        if (sampleCount > maxCount) {
            throw ToneGeneratorException(
                "$maxCount samples generated without zero crossing"
            )
        }
        val1 = sine1.next()
        val2 = sine2.next()
        return (val1 + val2) / 2.0
    }
}

final class ToneScaler(generator: ToneGenerator) {
    private val toneGenerator = generator
    private val scaleFactor = Int.MAX_VALUE.toDouble()

    public fun next(): Int {
        return (toneGenerator.next() * scaleFactor).toInt()
    }
}
