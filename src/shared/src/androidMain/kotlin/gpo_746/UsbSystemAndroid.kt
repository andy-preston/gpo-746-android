package gpo_746

class UsbSystemAndroid() : UsbSystemInterface {

    override public fun open() {
        throw Exception("Not implemented")
    }

    override public fun close() {
        throw Exception("Not implemented")
    }

    override public fun bulkRead(): Array<UByte> {
        throw Exception("Not implemented")
    }

    override public fun read(
        requestCode: UByte,
        addressOrPadding: UShort
    ): Array<UByte> {
        throw Exception("Not implemented")
    }

    override public fun write(
        requestCode: UByte,
        addressOrValue: UShort,
        valueOrPadding: UShort
    ) {
        throw Exception("Not implemented")
    }
}
