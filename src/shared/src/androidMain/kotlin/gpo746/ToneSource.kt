package andyp.gpo746

import android.media.AudioTrack

abstract class AbstractToneSource(outputBufferSize: Int) {

    protected val outputSize = outputBufferSize

    private var sourcePointer: Int = 0

    protected abstract val source: ByteArray

    protected abstract fun streamWrite(
        buffer: ByteArray,
        offset: Int,
        length: Int
    ): Int

    protected fun writeBlock(required: Int): Int {
        val available = source.size - sourcePointer
        val blockSize = if (available > required) required else available
        val written = streamWrite(source, sourcePointer, blockSize)
        sourcePointer = sourcePointer + written
        if (sourcePointer >= source.size) {
            sourcePointer = 0
        }
        return written
    }

    public fun nextBlock(): Int = writeBlock(outputSize)

    public fun fillBuffer() {
        var required = outputSize
        while (required > 0) {
            val written = writeBlock(required)
            required = required - written
        }
    }
}

final class ToneSource(
    outputBufferSize: Int,
    outputAudioTrack: AudioTrack,
    sourceBuffer: ByteArray
) : AbstractToneSource(outputBufferSize) {

    private val audioTrack = outputAudioTrack

    protected override val source = sourceBuffer

    protected override fun streamWrite(
        buffer: ByteArray,
        offset: Int,
        length: Int
    ): Int = audioTrack.write(buffer, offset, length, AudioTrack.WRITE_BLOCKING)
}
