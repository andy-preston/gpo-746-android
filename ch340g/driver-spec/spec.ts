import { lcr, gclOutputBit, baudRate1, baudRate2} from './register.ts';
import { hex } from './hex.ts';
import { Method, Property, Specification, generator } from "./generator.ts";

const specification: Specification = (method: Method, property: Property) => {

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
    ).read(
        // My old libusb code had nothing about version in it
        "Read Version",
        "VendorGetVersion",
        "zero",
        "version"
    ).check(
        "version",
        // This chip in my prototype hardware is 0031
        // But some of the stuff I've seen in BSD drivers wants version >= 0030
        "0x0031"
    ).write(
        "Vendor Serial Init (0,0)",
        "VendorSerialInit",
        "zero",
        "0x00"
    ).write(
       // Both BSDs Set baud rate before enabling TX RX
       "Set Baud 1",
        "VendorWriteRegisters",
        "BaudRate1",
        baudRate1["9600"]
    ).write(
        "Set Baud 2",
        "VendorWriteRegisters",
        "BaudRate2",
        baudRate2["9600"]
    ).write(
        "Setup LCR",
        "VendorWriteRegisters",
        "LCR",
        lcr(["enableTX", "enableRX", "CS8"], ["parityNone"])
        // After this step, FreeBSD and mik3y does:
        // controlOut(VENDOR_SERIAL_INIT, 0x501f, 0xd90a);
        // It's not clear why
    ).end();

    method(
        "setHandshake"
    ).defineVariable(
        { name: "modemControl", type: "byte" },
        "0x00"
    ).ifConditionSetBit(
        "dtr",
        "modemControl",
        hex(gclOutputBit.DTR)
    ).ifConditionSetBit(
        "rts",
        "modemControl",
        hex(gclOutputBit.RTS)
    ).invertBits(
        "modemControl"
    ).writeControl(
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
