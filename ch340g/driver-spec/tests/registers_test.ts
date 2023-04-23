import { assertEquals } from "https://deno.land/std@0.184.0/testing/asserts.ts";
import { registerPairBits, lcr1bits, lcr2bits } from "../register.ts";
import { hex } from "../hex.ts";

Deno.test(
    "registerPairBits accepts all lcr1bits and lcr2bits as valid",
    () => {
        const expectedLcr1 =
            lcr1bits.CS5 | lcr1bits.CS6 | lcr1bits.CS7 | lcr1bits.CS8 |
            lcr1bits.parityEnable | lcr1bits.enableTX | lcr1bits.enableRX;
        const expectedLcr2 =
            lcr2bits.parityNone | lcr2bits.parityEven | lcr2bits.parityOdd |
            lcr2bits.parityMark | lcr2bits.paritySpace;
        const expectedValue = hex(expectedLcr1 | (expectedLcr2 << 8));
        const result = registerPairBits(
            ["CS5", "CS6", "CS7", "CS8", "parityEnable", "enableTX", "enableRX"],
            lcr1bits,
            ["parityNone", "parityEven", "parityOdd", "parityMark", "paritySpace"],
            lcr2bits
        );
        assertEquals(expectedValue, result);
    }
);

Deno.test(
    "registerPairBits doesn't allow gibberish in low bits",
    () => {
        try {
            registerPairBits(
                ["plop"],
                lcr1bits,
                ["parityNone"],
                lcr2bits
                );
        }
        catch (error) {
            assertEquals("Invalid bit plop", error.message);
            assertEquals(lcr1bits, error.choices);
        }
    }
);

Deno.test(
    "registerPairBits doesn't allow gibberish in high bits",
    () => {
        try {
            registerPairBits(
                ["CS8"],
                lcr1bits,
                ["plop"],
                lcr2bits
                );
        }
        catch (error) {
            assertEquals("Invalid bit plop", error.message);
            assertEquals(lcr2bits, error.choices);
        }
    }
);

Deno.test(
    "registerPairBits can process empty high bit array",
    () => {
        const expectedValue = hex(lcr1bits.CS8);
        const result = registerPairBits(
            ["CS8"],
            lcr1bits,
            [],
            lcr2bits
        );
        assertEquals(expectedValue, result);
    }
);

Deno.test(
    "registerPairBits can process empty low bit array",
    () => {
        const expectedValue = hex(lcr2bits.parityNone << 8);
        const result = registerPairBits(
            [],
            lcr1bits,
            ["parityNone"],
            lcr2bits
        );
        assertEquals(expectedValue, result);
    }
);
