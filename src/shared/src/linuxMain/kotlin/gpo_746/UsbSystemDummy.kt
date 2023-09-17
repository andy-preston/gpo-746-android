package gpo_746

class UsbSystemDummy : UsbSystemInterface {
    override fun open() {
        println("Open")
    }

    override fun close() {
        println("Close")
    }

    override fun bulkRead(): Array<UByte> {
        println("Bulk Read")
        return Array(4) { _ -> 0x23u }
    }

    override fun read(request: UByte, addressOrPadding: UShort): UShort {
        println("Read")
        return 0x23u
    }

    override fun write(request: UByte, addressOrValue: UShort, valueOrPadding: UShort) {
        println("Write")
    }

}