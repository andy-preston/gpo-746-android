package gpo_746

interface UsbSystemInterface {
    public fun open(timeout: Int)
    public fun close()
    public fun bulkRead(): Array<UByte>
    public fun read(requestCode: UByte, addressOrPadding: UShort): Array<UByte>
    public fun write(requestCode: UByte, addressOrValue: UShort, valueOrPadding: UShort)
}
