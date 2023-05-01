type HexDigit = "0"|"1"|"2"|"3"|"4"|"5"|"6"|"7"|"8"|"9"|"A"|"B"|"C"|"D"|"E"|"F";

export type HexNumber = `0x${HexDigit}${HexDigit}`|
    `0x${HexDigit}${HexDigit}${HexDigit}${HexDigit}`;

export const hex82 = (lowByte: number, highByte: number): HexNumber =>
    "0x" + (
        (
            highByte.toString(16).padStart(2, '0')
        ) + (
            lowByte.toString(16).padStart(2, '0')
        )
    ).toUpperCase() as HexNumber;


export const hex16 = (value: number): HexNumber =>
    "0x" + value.toString(16).toUpperCase().padStart(4, "0") as HexNumber;
