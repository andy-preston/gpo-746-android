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

    private var count: Int = 0

    public fun sampleCount() = count

    protected fun resetCount() {
        count = 0
    }

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

    private var cycles = 0

    public fun cyclesCompleted() = cycles

    protected override fun calculate(): Double {
        theta = theta + deltaTheta
        while (theta > TWO_PI) {
            theta = theta - TWO_PI
            cycles = cycles + 1
        }
        return sin(theta).toBigDecimal().setScale(
            PRECISION_DIGITS,
            RoundingMode.HALF_EVEN
        ).toDouble()
    }
}

final class Chopper(
    tg: ToneGenerator,
    chopTenthsSecond: Int
) : CountingGenerator(tg.samplingFrequency) {

    private val generator = tg

    @Suppress("MagicNumber")
    private val chopSamples = (chopTenthsSecond * tg.samplingFrequency) / 10

    private var chopping = false

    private var cycle = 0

    public fun cyclesCompleted() = cycle

    protected override fun calculate(): Double {
        val result = if (chopping) 0.0 else generator.next()
        if (sampleCount() == chopSamples) {
            resetCount()
            if (chopping) {
                cycle = cycle + 1
            }
            chopping = !chopping
        }
        return result
    }
}

final class Modulator(
    tg1: ToneGenerator,
    tg2: ToneGenerator,
) : CountingGenerator(tg1.samplingFrequency) {

    private val generator1 = tg1

    private val generator2 = tg2

    init {
        if (tg1.samplingFrequency != tg2.samplingFrequency) {
            throw ToneGeneratorException(
                "Sampling frequencies ${tg1.samplingFrequency} != ${tg2.samplingFrequency}"
            )
        }
    }

    public fun bothZeroCrossing(): Boolean {
        return sampleCount() > 0 &&
            generator1.thisVal() == 0.0 &&
            generator2.thisVal() == 0.0
    }

    protected override fun calculate(): Double {
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

@Suppress("MagicNumber")
final class Tones(sampleFrequency: Int) {

    private val samplingFrequency = sampleFrequency

    public fun dial(): Sequence<Int> {
        val modulator = Modulator(
            Sine(350, samplingFrequency),
            Sine(450, samplingFrequency)
        )
        val scaler = ToneScaler(modulator)
        return sequence {
            while (!modulator.bothZeroCrossing()) {
                yield(scaler.next())
            }
        }
    }

    public fun engaged(): Sequence<Int> {
        val chopper = Chopper(
            Sine(400, samplingFrequency),
            4
        )
        val scaler = ToneScaler(chopper)
        return sequence {
            while (chopper.cyclesCompleted() < 1) {
                yield(scaler.next())
            }
        }
    }

    public fun misdial(): Sequence<Int> {
        val sine = Sine(400, samplingFrequency)
        val scaler = ToneScaler(sine)
        return sequence {
            while (sine.cyclesCompleted() < 1) {
                yield(scaler.next())
            }
        }
    }
}
