export default function(cpuClockFrequency, baudRate) {
    const baudRateMultiplier = baudRate * 16;
    const baudPrescale = Math.round(cpuClockFrequency / baudRateMultiplier) - 1
    const derivedBaud = cpuClockFrequency / (16 * (baudPrescale + 1))
    return [
        "",
        "    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;",
        "",
        "    ; Baud Rate = " + baudRate,
        "    ; CPU Clock Frequency = " + cpuClockFrequency,
        "    ; Baud Rate * 16 = " + baudRateMultiplier,
        "    ; baudPrescale = (" + cpuClockFrequency + " / " +
            baudRateMultiplier + ") - 1",
        "    ; Derived Baud Rate = " + derivedBaud,
        "    .equ baudPrescale = " + baudPrescale
    ];
}
