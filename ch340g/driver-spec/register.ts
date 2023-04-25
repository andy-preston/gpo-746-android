import { hex, type HexNumber } from './hex.ts';

const dummyRegister = "0000"
const lcr1Low = "18", lcr2High = "25";
const gcl1Low = "06", gcl2High = "07"; // AKA "status"
const bpsPrescaleLow1 = "12", bpsDivisorHigh1 = "13";
const bpsModLow1 = "14", bpsPaddingHigh2 = "0F"; // some drivers use 2C not 14

export const readRegister = {
    zero: `0x${dummyRegister}`,
    // When NET BSD reads GCL, it only uses gcl1Low - the first byte
    // When mik3y reads GCL, it only returns the first byte from the buffer
    // When felHR85 reads GCL, it only uses the first byte
    GCL: `0x${gcl2High}${gcl1Low}`,
} as const;

export type ReadRegisterName = keyof typeof readRegister;
export type ReadRegisterAddress = typeof readRegister[ReadRegisterName];

export const writeRegister = {
    zero: `0x${dummyRegister}`,
    BaudRate1: `0x${bpsDivisorHigh1}${bpsPrescaleLow1}`,
    BaudRate2: `0x${bpsPaddingHigh2}${bpsModLow1}`,
    LCR: `0x${lcr2High}${lcr1Low}`,
    // Only NetBSD writes GCL at all - it writes the same value to gcl1Low twice
    GCL: `0x${gcl1Low}${gcl1Low}`,
} as const;

export type WriteRegisterName = keyof typeof writeRegister;
export type WriteRegisterAddress = typeof writeRegister[WriteRegisterName];

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

const registerPairBits = (
    lowBitsToSet: Array<string>,
    lowSpecification: Record<string, number>,
    highBitsToSet: Array<string>,
    highSpecification: Record<string, number>
): HexNumber => hex(registerPairBitsNumeric(
        lowBitsToSet,
        lowSpecification,
        highBitsToSet,
        highSpecification
    ));

export const baudRate1 = {
    "2400": "0xD901",
    "4800": "0x6402",
    "9600": "0xB202",
    "19200": "0xD902",
    "38400": "0x6403",
    "115200": "0xCC03"
} as const;

export const baudRate2 = {
    "2400": "0x0038",
    "4800": "0x001F",
    "9600": "0x0013",
    "19200": "0x000D",
    "38400": "0x000A",
    "115200": "0x0008"
} as const;

const lcr1bits = {
    "CS5": 0x00, // Not defined in FreeBSD, only in NetBSD
    "CS6": 0x01, // Not defined in FreeBSD, only in NetBSD
    "CS7": 0x02, // Not defined in FreeBSD, only in NetBSD
    "CS8": 0x03, // The only one in FreeBSD
    "parityEnable": 0x08,
    "enableTX": 0x40,
    "enableRX": 0x80,
} as const;

const lcr2bits = {
    "parityNone": 0x00,
    "parityEven": 0x07,  // FreeBSD says 0x07 Linux & NetBSD says 0x10
    "parityOdd": 0x06,   // FreeBSD says 0x06         NetBSD says 0x00
    "parityMark": 0x05,  // FreeBSD says 0x05         NetBSD says 0x20
    "paritySpace": 0x04, // FreeBSD says 0x04         NetBSD says 0x30
} as const;

export const lcr = (
    lowBitsToSet: Array<keyof typeof lcr1bits>,
    highBitsToSet: Array<keyof typeof lcr2bits>
): HexNumber => registerPairBits(lowBitsToSet, lcr1bits, highBitsToSet, lcr2bits);

export const gclInputBit = {
    "CTS": 0x01,
    "DSR": 0x02,
    "RI": 0x04,
    "DCD": 0x08
} as const;

export const gclOutputBit = {
    "DTR": 0x20, // 1 << 5
    "RTS": 0x40 // 1 << 6
} as const;

/*
export const gcl = (

)
*/
