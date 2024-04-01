package andyp.gpo746

import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TestToneSource(
    outputBufferSize: Int,
    sourceBytes: ByteArray
) : AbstractToneSource(outputBufferSize) {

    protected override val source = sourceBytes

    private val stream = ByteArrayOutputStream()

    protected override fun streamWrite(
        buffer: ByteArray,
        offset: Int,
        length: Int
    ): Int {
        stream.write(buffer, offset, length)
        return length
    }

    public fun outputBytes(): ByteArray = stream.toByteArray()
}

class ToneSourceTest {

    private val sourceBytes = byteArrayOf(-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5)

    @Test
    public fun buffer_can_by_filled_and_will_not_overflow() {
        val expectedSize = 128
        val source = TestToneSource(expectedSize, sourceBytes)

        val wholeCopiesThatFit = Math.floor(
            expectedSize.toDouble() / sourceBytes.size.toDouble()
        ).toInt()
        assertTrue(wholeCopiesThatFit * sourceBytes.size < expectedSize)

        source.fillBuffer()
        assertEquals(expectedSize, source.outputBytes().size)
    }

    @Test
    public fun buffer_contains_repeated_copies_of_source_with_wrap_around() {
        val source = TestToneSource(64, sourceBytes)
        (1..3).forEach {
            source.nextBlock()
        }
        var sourcePos = 0
        var chunk = 1
        source.outputBytes().forEachIndexed { pos, value ->
            assertSame(
                sourceBytes[sourcePos],
                value,
                "Chunk: $chunk, buffer pos: $pos, source pos: $sourcePos"
            )
            if (sourcePos < sourceBytes.size - 1) {
                sourcePos = sourcePos + 1
            } else {
                sourcePos = 0
                chunk = chunk + 1
            }
        }
    }

    @Test
    public fun if_buffer_is_smaller_than_source_consecutive_fills_will_use_consecutive_chunks() {
        val source = TestToneSource(sourceBytes.size / 4, sourceBytes)
        (1..16).forEach {
            source.nextBlock()
        }
        var sourcePos = 0
        var chunk = 1
        source.outputBytes().forEachIndexed { pos, value ->
            assertSame(
                sourceBytes[sourcePos],
                value,
                "Chunk: $chunk, buffer pos: $pos, source pos: $sourcePos"
            )
            if (sourcePos < sourceBytes.size - 1) {
                sourcePos = sourcePos + 1
            } else {
                sourcePos = 0
                chunk = chunk + 1
            }
        }
    }
}
