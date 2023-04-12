import { Register, RegisterPair, RequestCode } from './enums.ts';
import { LCR1Bit } from './LCR.ts';
import { LanguageFlag, Method, Specification, generator } from "./generator.ts";

const baudRateLookup = (
    baudRate: 2400|4800|9600|19200|38400|115200
): Array<number> => {
    // There's also a function "somewhere" that calculates these values rather
    // than just look them up. It would be good to have both and test their
    // results are consistent.
    switch (baudRate) {
        case 2400: return [0xd901, 0x0038];
        case 4800: return [0x6402, 0x001f];
        case 9600: return [0xb202, 0x0013];
        case 19200: return [0xd902, 0x000d];
        case 38400: return [0x6403, 0x000a];
        case 115200: return [0xcc03, 0x0008];
    }
}

const specification: Specification = (method: Method) => {

    method(
        "getVersion"
    ).input(
        "Get Version",
        RequestCode.VendorGetVersion,
        0,
        "version"
    ).check(
        "version",
        0x0031
    ).end();

    let baud1, baud2;
    [baud1, baud2] = baudRateLookup(9600);

    method(
        "initialise"
    ).output(
        "vendorSerialInit (0,0)",
        RequestCode.VendorSerialInit,
        Register.zero,
        0
    ).output(
        // Both BSDs Set baud rate before enabling TX RX
        "LCR setup",
        RequestCode.VendorWriteRegisters,
        Register.LCR1,
        LCR1Bit.enableTX | LCR1Bit.enableRX | LCR1Bit.CS8
    ).output(
        "Baud 1",
        RequestCode.VendorWriteRegisters,
        RegisterPair.BaudRate1,
        baud1
    ).output(
        "Baud 2",
        RequestCode.VendorWriteRegisters,
        RegisterPair.BaudRate2,
        baud2
    ).end();
}

generator(LanguageFlag.C, 1000, specification);
