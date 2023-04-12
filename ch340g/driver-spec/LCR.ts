import { assert } from './values.ts';

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
