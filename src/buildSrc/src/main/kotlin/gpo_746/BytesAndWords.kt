abstract class BytesAndWords {
    protected fun wordFromBytes(highByte: Int, lowByte: Int): Int {
        return lowByte * 256 + highByte
    }
}
