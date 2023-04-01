export default function(
    cpuClockFrequency: number,
    baudRate: number
): Array<string> {
    const multiplier: number = baudRate * 16;
    const prescaler: number = Math.round(cpuClockFrequency / multiplier) - 1
    const derived: number = cpuClockFrequency / (16 * (prescaler + 1))
    if (derived != baudRate) {
        throw RangeError("Imperfect baud rate");
    }
    return [
        "",
        "    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;",
        "",
        "    ; Baud Rate = " + baudRate,
        "    ; CPU Clock Frequency = " + cpuClockFrequency,
        "    ; Baud Rate * 16 = " + multiplier,
        "    ; Prescaler = (" + cpuClockFrequency + " / " + multiplier + ") - 1",
        "    ; Derived Baud Rate = " + derived,
        "    .equ baudPrescaler = " + prescaler
    ];
}
