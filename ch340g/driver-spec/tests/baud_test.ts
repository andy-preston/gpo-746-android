import { assertEquals } from "https://deno.land/std@0.184.0/testing/asserts.ts";
import { baudRate1, baudRate2 } from "../register.ts";
import { HexNumber } from "../hex.ts";

Deno.test("Free BSD BPS values = 'predicted' values", () => {
    const dividers = {
        "2400": {
            baseClock: 93750,
            prescale: 1,
            div: 0,
            mod: 0,
        },
        "4800": {
            baseClock: 750000,
            prescale: 2,
            div: 0,
            mod: 0,
        },
        "9600": {
            baseClock: 750000,
            prescale: 2,
            div: 0,
            mod: 0,
        },
        "19200": {
            baseClock: 750000,
            prescale: 2,
            div: 0,
            mod: 0,
        },
        "38400": {
            baseClock: 6000000,
            prescale: 3,
            div: 0,
            mod: 0,
        },
        "115200": {
            baseClock: 6000000,
            prescale: 3,
            div: 0,
            mod: 0
        },
    };

    const toHex = (lowByte: number, highByte: number): HexNumber =>
        "0x" + (
            (
                highByte.toString(16).padStart(2, '0')
            ) + (
                lowByte.toString(16).padStart(2, '0')
            )
        ).toUpperCase() as HexNumber;

    type BaudRate1Index = keyof typeof baudRate1;
    type BaudRate2Index = keyof typeof baudRate2;

    for (const [rateStr, divider] of Object.entries(dividers)) {
        const rate = parseInt(rateStr);
        const prescale = divider.prescale;
        let div = Math.floor(divider.baseClock / rate);
        const rem = divider.baseClock % rate;
        if (div == 0 || div >= 0xFF) {
            throw "Bad divisor";
        }
	    if ((rem << 1) >= rate) {
            div = div + 1;
        }
	    div = 256 - div;
    	let mod = Math.floor(20000000 / rate) + 1100;
	    mod = mod + Math.floor(mod / 2);
	    mod = Math.floor((mod + 0xFF) / 0x100);

        assertEquals(
            toHex(prescale, div),
            baudRate1[rateStr as BaudRate1Index],
            `prescale and div matching for ${rateStr}`
        );
        assertEquals(
            toHex(mod, 0),
            baudRate2[rateStr as BaudRate2Index],
            `mod matching for ${rateStr}`
        );
    }

});
