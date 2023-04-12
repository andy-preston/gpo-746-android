export enum RequestCode {
    // This exists in the NetBSD and Linux drivers, but not FreeBSD
    VendorGetVersion = 0x5F,

    // Does this work on single registers or register pairs
    // or is it clever enough to do both?!
    VendorReadRegisters = 0x95,

    // Does this work on single registers or register pairs
    // or is it clever enough to do both?!
    VendorWriteRegisters = 0x9A,

    // Init or Reset - I suppose it could be both
    VendorSerialInit = 0xA1,

    VendorModemControl = 0xA4
}

export enum Register {
    zero = 0x00, // Seems to be needed for vendorSerialInit
    GCL1 = 0x06, // AKA STATUS
    GCL2 = 0x07, // AKA STATUS
    LCR1 = 0x18,
    LCR2 = 0x25,
}

export enum RegisterPair {
    // REG_BREAK1 0x05
    BaudRate1 = 0x1312, // Prescaler 12 - Divisor 13
    BaudRate2 = 0x0F2C,
    // REG_BPS_MOD 0x14
    // REG_BPS_PAD 0x0F
}

export enum SystemType { linux, android }
