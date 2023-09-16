package gpo_746

class UsbSystemAndroid : UsbSystemInterface {
    override fun open() {
        throw Exception("Not implemented")
    }

    override fun close() {
        throw Exception("Not implemented")
    }

    override fun bulkRead(): ByteArrayOrFailure {
        throw Exception("Not implemented")
    }

    override fun read(
        title: String,
        request: ReadRequest,
        register: ReadRegister
    ): ByteOrFailure {
        throw Exception("Not implemented")
    }

    override fun write(
        title: String,
        request: WriteRequest,
        register: WriteRegister,
        value: Short
    ): NullOrFailure {
        throw Exception("Not implemented")
    }

}