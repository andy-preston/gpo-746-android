package andyp.gpo746

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
interface UsbSystemInterface {
    public fun start(vid: UShort, pid: UShort, timeout: Int)
    public fun finish()
    public fun bulkRead(): ByteArray
    public fun read(requestCode: UByte, addressOrPadding: UShort): ByteArray
    public fun write(requestCode: UByte, addressOrValue: UShort, valueOrPadding: UShort)
}
