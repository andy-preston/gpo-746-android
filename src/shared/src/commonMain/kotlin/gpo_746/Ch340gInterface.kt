package gpo_746

sealed class ByteOrFailure {
    class Success(val value: UByte): ByteOrFailure()
    class Failure(val message: String): ByteOrFailure()
}

sealed class ByteArrayOrFailure {
    class Success(val value: Array<UByte>): ByteArrayOrFailure()
    class Failure(val message: String): ByteArrayOrFailure()
}

sealed class NullOrFailure {
    class Success(val value: UByte?): NullOrFailure()
    class Failure(val message: String): NullOrFailure()
}

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
    readDummy(0x0000u),
    // When NetBSD reads GCL, it only uses gcl1Low - the first byte
    // When mik3y reads GCL, it only returns the first byte from the buffer
    // When felHR85 reads GCL, it only uses the first byte
    readGcl(0x0706u)
}

//val baudModLow1 = "14", baudPaddingHigh2 = "0F"; // some drivers use 2C not 14

enum class WriteRegister(val address: UShort) {
    writeDummy(0x0000u),
    baudDivisorPrescale(0x1312u),
    baudMod(0x0F14u),
    lcr(0x2518u),
    // Only NetBSD writes GCL at all - it writes the same value to gcl1Low twice
    writeGcl(0x0606u)
}

interface Ch340gInterface {
    fun open()

    fun close()

    fun bulkRead(): ByteArrayOrFailure

    fun read(
        title: String,
        request: ReadRequest,
        register: ReadRegister
    ): ByteOrFailure

    fun write(
        title: String,
        request: WriteRequest,
        register: WriteRegister,
        value: Short
    ): NullOrFailure
}
