type HexDigit = "0"|"1"|"2"|"3"|"4"|"5"|"6"|"7"|"8"|"9"|"A"|"B"|"C"|"D"|"E"|"F";

export type HexNumber = `0x${HexDigit}${HexDigit}`|
    `0x${HexDigit}${HexDigit}${HexDigit}${HexDigit}`;

export const hex = (value: number): HexNumber =>
    "0x" + value.toString(16).toUpperCase().padStart(4, "0") as HexNumber;
