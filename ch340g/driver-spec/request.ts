export const requestCode = {
    // This exists in the NetBSD and Linux drivers, but not FreeBSD
    VendorGetVersion: "0x5F",
    // Does this work on single registers or register pairs
    // or is it clever enough to do both?!
    VendorReadRegisters: "0x95",
    // Does this work on single registers or register pairs
    // or is it clever enough to do both?!
    VendorWriteRegisters: "0x9A",
    // Init or Reset - I suppose it could be both
    VendorSerialInit: "0xA1",
    // To handle handshaking on version >= 0x20
    VendorModemControl: "0xA4"
} as const;

export type RequestName = keyof typeof requestCode;
export type RequestCode = typeof requestCode[RequestName];
