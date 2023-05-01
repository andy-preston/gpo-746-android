import { HexNumber, hex82 } from "./hex.ts";

const basis = {
    "2400": { clock: 93750, prescale: 1 },
    "4800": { clock: 750000, prescale: 2 },
    "9600": { clock: 750000, prescale: 2 },
    "19200": { clock: 750000, prescale: 2 },
    "38400": { clock: 6000000, prescale: 3 },
    "115200": { clock: 6000000, prescale: 3 },
};

export type BaudRate = keyof typeof basis;

type baudRateValuesKeys = "divisorPrescale" | "mod";

export const baudRateValues = (
    rateStr: BaudRate
): Record<baudRateValuesKeys, HexNumber> => {
    const rate = parseInt(rateStr);

    const prescale = basis[rateStr].prescale;

    const remainder = basis[rateStr].clock % rate;

    let dividend = Math.floor(basis[rateStr].clock / rate);
    if (dividend == 0 || dividend >= 0xFF) {
        throw "Baud rate divider overflow!";
    }
	if (remainder * 2 >= rate) {
        dividend = dividend + 1;
    }
	dividend = 0x0100 - dividend; // equivalent of negative 8 bit

    // I found myself asking why of every step and value in this part.
    // This comes from the FreeBSD driver...
    // not that I think rereading it will help that much
    let mod = Math.floor(20000000 / rate) + 1100;
	mod = mod + Math.floor(mod / 2);
	mod = Math.floor((mod + 0xFF) / 0x100);

    return {
        "divisorPrescale": hex82(prescale, dividend),
        "mod": hex82(mod, 0),
    }
}
