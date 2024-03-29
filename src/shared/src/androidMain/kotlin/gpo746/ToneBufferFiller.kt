package andyp.gpo746

import java.io.ByteArrayOutputStream

abstract class ToneBufferFiller {

    private var sourcePointer: Int = 0

    protected abstract val bufferSize: Int

    protected fun setupSamples(source: ByteArray): ByteArray {
        val stream = ByteArrayOutputStream()
        var needed = bufferSize
        while (needed > 0) {
            val available = source.size - sourcePointer
            val write = if (needed > available) available else needed
            stream.write(source, sourcePointer, write)
            needed = needed - write
            sourcePointer = sourcePointer + write
            if (sourcePointer > (source.size - 1)) {
                sourcePointer = 0
            }
        }
        return stream.toByteArray()
    }
}
