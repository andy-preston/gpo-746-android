import { assert } from './values.ts';

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
    // To handle handshaking on version >= 0x20
    VendorModemControl = 0xA4
}

export enum Register {
    zero = 0x00, // Seems to be needed for vendorSerialInit
    GCL1 = 0x06, // AKA STATUS
    GCL2 = 0x07, // AKA STATUS
    LCR1 = 0x18,
    LCR2 = 0x25
}

export enum RegisterPair {
    // REG_BREAK1 0x05
    BaudRate1 = 0x1312, // Prescaler 12 - Divisor 13
    BaudRate2 = 0x0F2C,
    LCR = Register.LCR1 | (Register.LCR2 << 8)
    // REG_BPS_MOD 0x14
    // REG_BPS_PAD 0x0F
}

export const LCR1Bit = {
    "CS5": 0x00, // Not defined in FreeBSD, only in NetBSD
    "CS6": 0x01, // Not defined in FreeBSD, only in NetBSD
    "CS7": 0x02, // Not defined in FreeBSD, only in NetBSD
    "CS8": 0x03, // The only one in FreeBSD
    "parityEnable": 0x08,
    "enableTX": 0x40,
    "enableRX": 0x80,
    "mask": function() {
        const dataBitsMask = LCR1Bit.CS5 | LCR1Bit.CS6 | LCR1Bit.CS7 | LCR1Bit.CS8;
        const enableMask = LCR1Bit.enableRX | LCR1Bit.enableTX;
        const mask = LCR1Bit.parityEnable | enableMask | dataBitsMask;
        assert ("LCR1 mask", 0xAF, mask);
        return mask;
    }
}

export const LCR2Bit = {
    "parityNone": 0x00,
    "parityEven": 0x07,  // FreeBSD says 0x07 Linux & NetBSD says 0x10
    "parityOdd": 0x06,   // FreeBSD says 0x06         NetBSD says 0x00
    "parityMark": 0x05,  // FreeBSD says 0x05         NetBSD says 0x20
    "paritySpace": 0x04, // FreeBSD says 0x04         NetBSD says 0x30
    "mask": function() {
        const parityMask = LCR2Bit.parityEven | LCR2Bit.parityOdd |
            LCR2Bit.parityMark | LCR2Bit.paritySpace;
        assert("LCR2 mask", 0x07, parityMask);
        return parityMask;
    }
};

export const GCLInputBit = {
    "CTS": 0x01,
    "DSR": 0x02,
    "RI": 0x04,
    "DCD": 0x08,
    "mask": function() {
        const mask = GCLInputBit.CTS | GCLInputBit.DSR | GCLInputBit.RI | GCLInputBit.DCD
        assert ("GCL input mask", 0xF0, mask);
        return mask;
    }
}

export const GCLOutputBit = {
    "DTR": 0x20, // 1 << 5
    "RTS": 0x40, // 1 << 6
    "mask": function() {
        const mask = GCLOutputBit.DTR | GCLOutputBit.RTS
        assert ("GCL output mask", 0x60, mask);
        return mask;
    }
}
