package gpo_746

class UsbSystemDummy : UsbSystemInterface {
    override fun open() {
        println("Open")
    }

    override fun close() {
        println("Close")
    }

    override fun bulkRead(): ByteArrayOrFailure {
        println("Bulk Read")
        return ByteArrayOrFailure.Success(Array(4) { _ -> 0x23u })
    }

    override fun read(
        title: String,
        request: ReadRequest,
        register: ReadRegister
    ): ByteOrFailure {
        println("Read")
        return ByteOrFailure.Success(0x23u)
    }

    override fun write(
        title: String,
        request: WriteRequest,
        register: WriteRegister,
        value: Short
    ): NullOrFailure {
        println("Write")
        return NullOrFailure.Success(null)
    }

}