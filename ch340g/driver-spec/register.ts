import { type HexNumber } from './hex.ts';

export const register = {
    zero: "0x00", // Seems to be needed for vendorSerialInit
    GCL1: "0x06", // AKA STATUS
    GCL2: "0x07", // AKA STATUS
    LCR1: "0x18",
    LCR2: "0x25",

    BaudRate1: "0x1312", // Prescaler 12 - Divisor 13
    BaudRate2: "0x0F2C",
    LCR: "0x2518"
} as const;

export type RegisterName = keyof typeof register;
export type RegisterAddress = typeof register[RegisterName];

const registerBitsNumeric = (
    bitsToSet: Array<string>,
    bitSpecification: Record<string, number>
): number => bitsToSet.reduce(
    (accumulator: number, bitToSet: string) => {
        if (!(bitToSet in bitSpecification)) {
            throw {
                message: `Invalid bit ${bitToSet}`,
                choices: bitSpecification
            };
        }
        return accumulator | bitSpecification[bitToSet];
    },
    0
);

const registerPairBitsNumeric = (
    lowBitsToSet: Array<string>,
    lowSpecification: Record<string, number>,
    highBitsToSet: Array<string>,
    highSpecification: Record<string, number>
): number => registerBitsNumeric(lowBitsToSet, lowSpecification) | (
    registerBitsNumeric(highBitsToSet, highSpecification) << 8
)

export const registerPairBits = (
    lowBitsToSet: Array<string>,
    lowSpecification: Record<string, number>,
    highBitsToSet: Array<string>,
    highSpecification: Record<string, number>
): HexNumber => "0x" + (
    registerPairBitsNumeric(
        lowBitsToSet,
        lowSpecification,
        highBitsToSet,
        highSpecification
    ).toString(16)
) as HexNumber

export const lcr1bits = {
    "CS5": 0x00, // Not defined in FreeBSD, only in NetBSD
    "CS6": 0x01, // Not defined in FreeBSD, only in NetBSD
    "CS7": 0x02, // Not defined in FreeBSD, only in NetBSD
    "CS8": 0x03, // The only one in FreeBSD
    "parityEnable": 0x08,
    "enableTX": 0x40,
    "enableRX": 0x80,
} as const;

export const lcr2bits = {
    "parityNone": 0x00,
    "parityEven": 0x07,  // FreeBSD says 0x07 Linux & NetBSD says 0x10
    "parityOdd": 0x06,   // FreeBSD says 0x06         NetBSD says 0x00
    "parityMark": 0x05,  // FreeBSD says 0x05         NetBSD says 0x20
    "paritySpace": 0x04, // FreeBSD says 0x04         NetBSD says 0x30
};

export const gclInputBit = {
    "CTS": 0x01,
    "DSR": 0x02,
    "RI": 0x04,
    "DCD": 0x08
}

export const gclOutputBit = {
    "DTR": 0x20, // 1 << 5
    "RTS": 0x40 // 1 << 6
}
