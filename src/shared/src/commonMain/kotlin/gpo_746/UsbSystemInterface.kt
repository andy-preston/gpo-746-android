package gpo_746

interface UsbSystemInterface {
    fun open()

    fun close()

    fun bulkRead(): Array<UByte>

    fun read(request: UByte, addressOrPadding: UShort): UShort

    fun write(request: UByte, addressOrValue: UShort, valueOrPadding: UShort)
}
