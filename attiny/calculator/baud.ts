export default function(
    cpuClockFrequency: number,
    baudRate: number
): string {
    const multiplier: number = baudRate * 16;
    const prescaler: number = Math.round(cpuClockFrequency / multiplier) - 1
    const derived: number = cpuClockFrequency / (16 * (prescaler + 1))
    if (derived != baudRate) {
        throw RangeError("Imperfect baud rate");
    }
    return `.equ baudPrescaler = ${prescaler}`;
}
