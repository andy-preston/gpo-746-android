package gpo_746

import android.hardware.usb.UsbInterface

class UsbSystemAndroid() : UsbSystemInterface {

    override public fun open(timeout: Int) {
        throw Exception("Not implemented")
    }

    private fun findReadEndpoint(dataInterface: UsbInterface) {
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
