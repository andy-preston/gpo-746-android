package gpo_746

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
