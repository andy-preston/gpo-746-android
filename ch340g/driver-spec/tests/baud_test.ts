import { assertEquals } from "https://deno.land/std@0.184.0/testing/asserts.ts";
import { BaudRate, baudRateValues } from "../baud.ts";

Deno.test(
    "Calculated values from Free BSD match lookup table from other drivers",
    () => {
        const expectation = {
            "2400": { divisorPrescale: "0xD901", mod: "0x0038" },
            "4800": { divisorPrescale: "0x6402", mod: "0x001F" },
            "9600": { divisorPrescale: "0xB202", mod: "0x0013" },
            "19200": { divisorPrescale: "0xD902", mod: "0x000D" },
            "38400": { divisorPrescale: "0x6403", mod: "0x000A" },
            "115200": { divisorPrescale: "0xCC03", mod: "0x0008" },
        } as const;
        for (const [rate, expected] of Object.entries(expectation)) {
            const calculated = baudRateValues(rate as BaudRate);
            assertEquals(
                expected.divisorPrescale,
                calculated.divisorPrescale,
                `prescale and div matching for ${rate}`
            );
            assertEquals(
                expected.mod,
                calculated.mod,
                `mod matching for ${rate}`
            );
        }
    }
);
