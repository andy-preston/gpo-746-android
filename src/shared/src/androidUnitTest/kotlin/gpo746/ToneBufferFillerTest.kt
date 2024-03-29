package andyp.gpo746

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TestToneBufferFiller(testBufferSize: Int) : ToneBufferFiller() {
    protected override val bufferSize = testBufferSize

    public fun testSetupSamples(
        source: ByteArray
    ): ByteArray = setupSamples(source)
}

class ToneBufferFillerTest {

    private val testSource = byteArrayOf(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5)

    @Test
    public fun buffer_will_by_filled_and_not_overflow() {
        val bufferSize = 128
        val filler = TestToneBufferFiller(bufferSize)
        val wholeCopiesThatFit = Math.floor(
            bufferSize.toDouble() / testSource.size.toDouble()
        ).toInt()
        val sizeOfWholeCopies = wholeCopiesThatFit * testSource.size
        assertTrue(
            sizeOfWholeCopies < bufferSize
        )
        assertEquals(
            bufferSize,
            filler.testSetupSamples(testSource).size
        )
    }

    @Test
    public fun buffer_contains_repeated_copies_of_source_with_wrap_around() {
        val bufferSize = 64
        val filler = TestToneBufferFiller(bufferSize)
        var source = 0
        for (chunk in 1..3) {
            filler.testSetupSamples(testSource).forEachIndexed { pos, value ->
                assertSame(
                    testSource[source],
                    value,
                    "Chunk: $chunk, buffer pos: $pos, source: $source"
                )
                source = if (source < testSource.size - 1) source + 1 else 0
            }
        }
    }
}
