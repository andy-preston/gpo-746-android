export default function(cpuClockFrequency, baudRate) {
    const multiplier = baudRate * 16;
    const prescale = Math.round(cpuClockFrequency / multiplier) - 1
    const derived = cpuClockFrequency / (16 * (prescale + 1))
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
        "    ; Prescale = (" + cpuClockFrequency + " / " + multiplier + ") - 1",
        "    ; Derived Baud Rate = " + derived,
        "    .equ baudPrescale = " + prescale
    ];
}
