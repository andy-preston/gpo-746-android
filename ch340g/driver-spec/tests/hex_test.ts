import { assertEquals } from "https://deno.land/std@0.184.0/testing/asserts.ts";
import { hex } from "../hex.ts";

Deno.test(
    "2 digit numbers are returned correctly",
    () => {
        ["0x0000", "0x000A", "0x000F", "0x0045", "0x003F", "0x00FF"].forEach(
            (stringRepresentation) => {
                const numericRepresentation = parseInt(stringRepresentation);
                assertEquals(stringRepresentation, hex(numericRepresentation));
            }
        );
    }
);

Deno.test(
    "4 digit numbers are returned correctly",
    () => {
        ["0x1200", "0x0505", "0x0A0A", "0xDEAD", "0xBEEF", "0xFFFF"].forEach(
            (stringRepresentation) => {
                const numericRepresentation = parseInt(stringRepresentation);
                assertEquals(stringRepresentation, hex(numericRepresentation));
            }
        );
    }
);
