package gpo_746

class UsbSystemLinux : UsbSystemInterface {
    override fun open() {
        throw Exception("Not implemented")
    }

    override fun close() {
        throw Exception("Not implemented")
    }

    override fun bulkRead(): Array<UByte> {
        throw Exception("Not implemented")
    }

    override fun read(request: UByte, addressOrPadding: UShort): UShort {
        throw Exception("Not implemented")
    }

    override fun write(request: UByte, addressOrValue: UShort, valueOrPadding: UShort) {
        throw Exception("Not implemented")
    }

}