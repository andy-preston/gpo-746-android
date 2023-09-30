package gpo_746

import android.hardware.usb.UsbInterface

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class UsbSystemProduction() : UsbSystemInterface {
    override public fun open(vid: UShort, pid: UShort, timeout: Int) {
        throw Exception("Not implemented")
    }

    private fun findReadEndpoint(dataInterface: UsbInterface) {
    }

    override public fun close() {
        throw Exception("Not implemented")
    }

    override public fun bulkRead(): UByteArray {
        throw Exception("Not implemented")
    }

    override public fun read(
        requestCode: UByte,
        addressOrPadding: UShort
    ): UByteArray {
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
