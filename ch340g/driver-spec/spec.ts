import { Register, RegisterPair, RequestCode } from './enums.ts';
import { LCR1Bit, LCR2Bit } from './LCR.ts';
import { LanguageFlag, Method, Specification, generator } from "./generator.ts";
import { VariableType } from './language_module.ts';

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

    // deno-lint-ignore prefer-const
    let baud1, baud2;
    [baud1, baud2] = baudRateLookup(9600);

    let LCR1Setting = LCR1Bit.enableTX | LCR1Bit.enableRX | LCR1Bit.CS8;
    let LCR2Setting = LCR2Bit.parityNone;

    method(
        "initialise"
    ).input(
        // My old libusb code had nothing about version in it
        "Get Version",
        RequestCode.VendorGetVersion,
        0,
        "version"
    ).check(
        "version",
        // This chip in my prototype hardware is 0031
        // But some of the stuff I've seen in BSD drivers wants version >= 0030
        0x0031
    ).output(
        "vendorSerialInit (0,0)",
        RequestCode.VendorSerialInit,
        Register.zero,
        0
    /*).output(
        // Both BSDs Set baud rate before enabling TX RX
        "LCR setup",
        RequestCode.VendorWriteRegisters,
        Register.LCR1,
        LCR1Setting */
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
    ).output(
        "Setup LCR",
        // if version < 0x30:
        //     readRegisters(LCR1, NULL, LCR2, NULL);
        //     // What is 0x50 or 0x00 - Got it from FreeBSD
        //     writeRegisters(LCR1, 0x50, LCR2, 0x00);
        // else:
        RequestCode.VendorWriteRegisters,
        RegisterPair.LCR,
        LCR1Setting | (LCR2Setting << 8)
        // After this step, FreeBSD and mik3y does:
        // controlOut(VENDOR_SERIAL_INIT, 0x501f, 0xd90a);
        // It's not clear why
    ).end();

    method(
        "setHandshake", [
            {name: "dtr", type: VariableType.boolean},
            {name: "rts", type: VariableType.boolean},
        ]
    ).defineVariable(
        {name: "modemControl", type: VariableType.byte},
        0
    ).ifConditionSetBit(
        "dtr",
        "modemControl",
        1 << 5, // 0x20
    ).ifConditionSetBit(
        "rts",
        "modemControl",
        1 << 6, // 0x40
    ).invertBits(
        "modemControl"
    ).modemControl(
        "set handshake",
        "modemControl"
    ).end();

generator(LanguageFlag.C, 1000, specification);
