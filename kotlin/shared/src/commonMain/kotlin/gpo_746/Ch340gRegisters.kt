package gpo_746

inline data class ReadRequest(val code: Byte)
val VendorGetVersion = ReadRequest(0x5F)
// Does this work on single registers or register pairs
// or is it clever enough to do both?!
val VendorReadRegisters = ReadRequest(0x95)

inline data class WriteRequest(val code: byte)
// Does this work on single registers or register pairs
// or is it clever enough to do both?!
val VendorWriteRegisters = WriteRequest(0x9A)
// Init or Reset - I suppose it could be both
val VendorSerialInit = WriteRequest(0xA1)
// To handle handshaking on version >= 0x20
val VendorModemControl = WriteRequest(0xA4)

inline data class ReadRegister(val address: Short)
val readDummy = ReadRegister(0x0000)
// When NET BSD reads GCL, it only uses gcl1Low - the first byte
// When mik3y reads GCL, it only returns the first byte from the buffer
// When felHR85 reads GCL, it only uses the first byte
val readGcl = ReadRegister(0x0706)

const baudModLow1 = "14", baudPaddingHigh2 = "0F"; // some drivers use 2C not 14

inline data class WriteRegister(val address: Short)
val writeDummy = WriteRegister(0x0000)
val baudDivisorPrescale = WriteRegister(0x1312),
val baudMod = WriteRegister(0x0F14)
val lcr = WriteRegister(0x2518)
// Only NetBSD writes GCL at all - it writes the same value to gcl1Low twice
val writeGcl = WriteRegister(0x0606)
