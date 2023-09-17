package gpo_746

enum class ReadRequest(val code: UByte) {
    vendorGetVersion(0x5Fu),
    // Does this work on single registers or register pairs
    // or is it clever enough to do both?!
    vendorReadRegisters(0x95u)
}

enum class WriteRequest(val code: UByte) {
    // Does this work on single registers or register pairs
    // or is it clever enough to do both?!
    vendorWriteRegisters(0x9Au),
    // Init or Reset - I suppose it could be both
    vendorSerialInit(0xA1u),
    // To handle handshaking on version >= 0x20
    vendorModemControl(0xA4u)
}

enum class ReadRegister(val address: UShort) {
    // When NetBSD reads GCL, it only uses gcl1Low - the first byte
    // When mik3y reads GCL, it only returns the first byte from the buffer
    // When felHR85 reads GCL, it only uses the first byte
    gcl(0x0706u)
}

//val baudModLow1 = "14", baudPaddingHigh2 = "0F"; // some drivers use 2C not 14

enum class WriteRegister(val address: UShort) {
    baudDivisorPrescale(0x1312u),
    baudMod(0x0F14u),
    lcr(0x2518u),
    // Only NetBSD writes GCL at all - it writes the same value to gcl1Low twice
    gcl(0x0606u)
}

enum class GclOutputBit(val mask: UByte) {
    DTR(0x20u), // 1 << 5
    RTS(0x40u)  // 1 << 6
}

enum class GclInputBit(val mask: UByte)  {
    CTS(0x01u),
    DSR(0x02u),
    RI(0x04u),
    DCD(0x08u)
}

class Ch340g(usbSystem: UsbSystemInterface) {

    private val usb = usbSystem

    // This is the version used in the prototype hardware
    // Some of the stuff I've seen in BSD drivers wants version >= 0030
    // There are also differences writing handshake with version < 0020
    private val usedChipVersion: UShort = 0x0031u

    private var version: UShort = 0u

    private fun write(request: WriteRequest, value: UShort) {
        usb.write(request.code, value, 0u)
    }

    private fun writeRegisters(register: WriteRegister, value: UShort) {
        usb.write(WriteRequest.vendorWriteRegisters.code, register.address, value)
    }

    private fun read(request: ReadRequest): UShort {
        return usb.read(request.code, 0u)
    }

    private fun readRegisters(register: ReadRegister): UShort {
        return usb.read(ReadRequest.vendorReadRegisters.code, register.address)
    }

    public fun writeHandshake(handshakeOutputRTS: Boolean) {
        // DTR isn't in use so it's bit is always set "off"
        var modemControl: UByte = if (handshakeOutputRTS) GclOutputBit.RTS.mask else 0u
        // How exactly is this done in the Unix drivers?
        // Do we send it zero padded? BE? LE? or repeated?
        write(WriteRequest.vendorModemControl, modemControl.inv().toUShort())
    }

    public fun readHandshake(): Boolean {
        val modemControl = readRegisters(ReadRegister.gcl)
        // should we be looking at high byte or low byte in returned value?
        val mask = GclInputBit.RI.mask.toUShort()
        return (modemControl and mask) == mask
    }

    public fun initialise() {
        version = read(ReadRequest.vendorGetVersion)
        if (version != usedChipVersion) {
            throw Exception("version should be ${usedChipVersion}, but it's ${version}")
        }
        write(WriteRequest.vendorSerialInit, 0u)
        writeRegisters(WriteRegister.baudDivisorPrescale, baud.divisorPrescale)
        writeRegisters(WriteRegister.baudMod, baud.mod)
        writeRegisters(WriteRegister.lcr, defaultLcr)
    }

}