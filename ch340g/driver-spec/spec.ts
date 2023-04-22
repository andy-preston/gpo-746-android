import { registerPairBits, lcr1bits, lcr2bits, gclOutputBit } from './register.ts';
import { type HexNumber } from './hex.ts';
import { Method, Property, Specification, generator } from "./generator.ts";

const baudRateLookup = (
    baudRate: 2400|4800|9600|19200|38400|115200
): Array<HexNumber> => {
    // There's also a function "somewhere" that calculates these values rather
    // than just look them up. It would be good to have both and test their
    // results are consistent.
    switch (baudRate) {
        case 2400: return ["0xD901", "0x0038"];
        case 4800: return ["0x6402", "0x001F"];
        case 9600: return ["0xB202", "0x0013"];
        case 19200: return ["0xD902", "0x000D"];
        case 38400: return ["0x6403", "0x000A"];
        case 115200: return ["0xCC03", "0x0008"];
    }
}

const specification: Specification = (method: Method, property: Property) => {

    // deno-lint-ignore prefer-const
    let baud1, baud2;
    [baud1, baud2] = baudRateLookup(9600);

    // output handshaking lines for GPIO
    property({ name: "dtr", type: "boolean" });
    property({ name: "rts", type: "boolean" });

    // input handshaking lines for GPIO
    property({ name: "cts", type: "boolean" });
    property({ name: "dsr", type: "boolean" });
    property({ name: "ri", type: "boolean" });
    property({ name: "dcd", type: "boolean" });

    method(
        "initialise"
    ).input(
        // My old libusb code had nothing about version in it
        "Get Version",
        "VendorGetVersion",
        "zero",
        "version"
    ).check(
        "version",
        // This chip in my prototype hardware is 0031
        // But some of the stuff I've seen in BSD drivers wants version >= 0030
        0x0031
    ).output(
        "vendorSerialInit (0,0)",
        "VendorSerialInit",
        "zero",
        "0x00"
    ).output(
       // Both BSDs Set baud rate before enabling TX RX
       "Baud 1",
        "VendorWriteRegisters",
        "BaudRate1",
        baud1
    ).output(
        "Baud 2",
        "VendorWriteRegisters",
        "BaudRate2",
        baud2
    ).output(
        "Setup LCR",
        // if version < 0x30:
        //     readRegisters(LCR1, NULL, LCR2, NULL);
        //     // What is 0x50 or 0x00 - Got it from FreeBSD
        //     writeRegisters(LCR1, 0x50, LCR2, 0x00);
        // else:
        "VendorWriteRegisters",
        "LCR",
        registerPairBits(
            ["enableTX", "enableRX", "CS8"], lcr1bits,
            ["parityNone"], lcr2bits
        )
        // After this step, FreeBSD and mik3y does:
        // controlOut(VENDOR_SERIAL_INIT, 0x501f, 0xd90a);
        // It's not clear why
    ).end();

    method(
        "setHandshake"
    ).defineVariable(
        { name: "modemControl", type: "byte" },
        0
    ).ifConditionSetBit(
        "dtr",
        "modemControl",
        gclOutputBit.DTR
    ).ifConditionSetBit(
        "rts",
        "modemControl",
        gclOutputBit.RTS
    ).invertBits(
        "modemControl"
    ).modemControl(
        "set handshake",
        "modemControl"
    ).end();

    method(
        "getHandshake"
    ).end();

    method(
        "readSerial"
    ).end();
}

generator("C", 1000, specification);
