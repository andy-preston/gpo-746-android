import { LanguageFlag, Method, Property, generator } from "./generator.ts";
import { HexNumber, hex16 } from "./hex.ts";
import { lcr, gcl, gclInputBit} from "./register.ts";
import { baudRateValues } from "./baud.ts";
import { bulkInputEndpoint } from "./endpoint.ts";

generator({
    language: Deno.args[0] as LanguageFlag,
    timeout: 1000,
    bufferSize: 16,
    buildSpec: (method: Method, property: Property) => {
        // This is the version used in the prototype hardware
        // Some of the stuff I've seen in BSD drivers wants version >= 0030
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
                lcr(["enableRX", "CS8"], ["parityNone"])
            )
            .end();

        method("writeHandshake")
            .defineVariable({ name: "modemControl", type: "byte" }, "0x00")
            .ifConditionSetBit("false", "modemControl", gcl(["DTR"]))
            .ifConditionSetBit("handshakeOutputRTS", "modemControl", gcl(["RTS"]))
            .invertBits("modemControl")
            .writeControl("modemControl")
            .end();

        method("readHandshake")
            .defineVariable({ name: "modemControl", type: "byte" }, "0x00")
            .read("VendorReadRegisters", "GCL", "modemControl")
            .setBooleanFromBit("handshakeInputRI","modemControl", hex16(gclInputBit.RI))
            .end();

        method("readSerial")
            .defineVariable({ name: "transferred", type: "integer"}, "0x00")
            .bulkRead(bulkInputEndpoint, "transferred")
            .end();
    }

});
