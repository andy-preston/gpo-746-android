export default function(
    cpuClockFrequency: number,
    baudRate: number
): string {
    const multiplier: number = baudRate * 16;
    const prescale: number = Math.round(cpuClockFrequency / multiplier) - 1
    const derived: number = cpuClockFrequency / (16 * (prescale + 1))
    if (derived != baudRate) {
        throw RangeError("Imperfect baud rate");
    }
    return `.equ baudPrescale = ${prescale}`;
}
