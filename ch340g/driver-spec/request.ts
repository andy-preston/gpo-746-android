export const readRequestCode = {
    VendorGetVersion: "0x5F",
    // Does this work on single registers or register pairs
    // or is it clever enough to do both?!
    VendorReadRegisters: "0x95",
} as const;

export type ReadRequestName = keyof typeof readRequestCode;
export type ReadRequestCode = typeof readRequestCode[ReadRequestName];

export const writeRequestCode = {
    // Does this work on single registers or register pairs
    // or is it clever enough to do both?!
    VendorWriteRegisters: "0x9A",
    // Init or Reset - I suppose it could be both
    VendorSerialInit: "0xA1",
    // To handle handshaking on version >= 0x20
    VendorModemControl: "0xA4"
} as const;

export type WriteRequestName = keyof typeof writeRequestCode;
export type WriteRequestCode = typeof writeRequestCode[WriteRequestName];
