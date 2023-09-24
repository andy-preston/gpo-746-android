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

        property({ name: "handshakeInputRI", type: "boolean" });
        property({ name: "version", type: "integer" });

        method("initialise")
            .setBoolean("handshakeInputRI", false)
            .read("VendorGetVersion", "zero", "version")
            .check("version", usedChipVersion)
            .write("VendorSerialInit", "zero", "0x00")
            .write("VendorWriteRegisters", "baudDivisorPrescale", baud.divisorPrescale)
            .write("VendorWriteRegisters", "baudMod", baud.mod)
            .write("VendorWriteRegisters", "LCR",
                lcr(["enableRX", "CS8"], ["parityNone"])
            )
            /* It's not clear why but, after this step, FreeBSD and mik3y does:
               controlOut(VENDOR_SERIAL_INIT, 0x501f, 0xd90a); */
            .end();

        method("writeHandshake", [{ name: "rts", type: "boolean" }])
            ////////////////////////////////////////////////////////////////////
            //
            // We need a word type to be an unsigned integer
            //
            ////////////////////////////////////////////////////////////////////
            .defineVariable({ name: "modemControl", type: "integer" }, "0x0000")
            .ifConditionSetBit("false", "modemControl", gcl(["DTR"]))
            .ifConditionSetBit("rts", "modemControl", gcl(["RTS"]))
            .invertBits("modemControl")
            .writeControl("modemControl")
            .end();

        // in Kotlin it can return true or false in ResultSuccess!
        // but in C that clashes with error reporting!!!
        method("readHandshake")
            .defineVariable({ name: "modemControl", type: "integer" }, "0x0000")
            .read("VendorReadRegisters", "GCL", "modemControl")
            .setBooleanFromBit("handshakeInputRI","modemControl", hex16(gclInputBit.RI))
            .end();

        ////////////////////////////////////////////////////////////////////////////
        //
        // We should be querying the device for it's endpoints - because
        // Android might make us do that.
        //
        ////////////////////////////////////////////////////////////////////////////

        method("readSerial")
            .defineVariable({ name: "transferred", type: "integer"}, "0x00")
            /*
            bulk transfer is synchronous in libusb - we should check for bytes
            available first.

            Or, if we can, just let it time out and return "0 bytes".
            */
            .bulkRead(bulkInputEndpoint, "transferred")
            .end();
    }

});
