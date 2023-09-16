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
