import { lcr, gcl, gclInputBit} from "./register.ts";
import { HexNumber, hex16 } from "./hex.ts";
import { Method, Property, Specification, generator } from "./generator.ts";
import { baudRateValues } from "./baud.ts";

const specification: Specification = (method: Method, property: Property) => {

    const usedChipVersion: HexNumber = "0x0031";

    const baud = baudRateValues("9600");

    property({ name: "handshakeOutputRTS", type: "boolean" });
    property({ name: "handshakeInputRI", type: "boolean" });
    property({ name: "version", type: "integer" });

    method("initialise")
        .setBoolean("handshakeInputRI", false)
        .setBoolean("handshakeOutputRTS", false)
        .read("VendorGetVersion", "zero", "version")
        .check("version", usedChipVersion)
        .write("VendorSerialInit", "zero", "0x00")
        .write("VendorWriteRegisters", "baudDivisorPrescale", baud.divisorPrescale)
        .write("VendorWriteRegisters", "baudMod", baud.mod)
        .write("VendorWriteRegisters", "LCR",
            lcr(["enableTX", "enableRX", "CS8"], ["parityNone"])
        )
        // After this step, FreeBSD and mik3y does:
        // controlOut(VENDOR_SERIAL_INIT, 0x501f, 0xd90a);
        // It's not clear why
        .end();

    method("setHandshake")
        .defineVariable({ name: "modemControl", type: "byte" }, "0x00")
        .ifConditionSetBit("false", "modemControl", gcl(["DTR"]))
        .ifConditionSetBit("handshakeOutputRTS", "modemControl", gcl(["RTS"]))
        .invertBits("modemControl")
        .writeControl("modemControl")
        .end();

}

generator("C", 1000, specification);
