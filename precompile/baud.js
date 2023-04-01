export default function(cpuClockFrequency, baudRate) {
    const multiplier = baudRate * 16;
    const prescaler = Math.round(cpuClockFrequency / multiplier) - 1
    const derived = cpuClockFrequency / (16 * (prescaler + 1))
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
