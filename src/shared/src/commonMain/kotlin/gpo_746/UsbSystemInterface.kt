package gpo_746

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
interface UsbSystemInterface {
    public fun open(vid: UShort, pid: UShort, timeout: Int)
    public fun close()
    public fun bulkRead(): UByteArray
    public fun read(requestCode: UByte, addressOrPadding: UShort): UByteArray
    public fun write(requestCode: UByte, addressOrValue: UShort, valueOrPadding: UShort)
}
