export default function(cpuClockFrequency) {

    const /* TCCR1B */ prescalarBits = function(prescalar) {
        return {
            0: "(1 << CS10)",
            8: "(1 << CS11)",
            64: "(1 << CS11) | (1 << CS10)",
            256: "(1 << CS12)",
            1024: "(1 << CS12) | (1 << CS10)"
        }[prescalar];
    }

    const halfPeriod = 20;
    const prescalar = 256;
    const timerFrequency = prescalar == 0 ? cpuClockFrequency :
        cpuClockFrequency / prescalar;
    const tick = (1 / timerFrequency) * 1000;
    const ringerTicks = halfPeriod / tick;
    if (ringerTicks > 0xffff || ringerTicks < 1) {
        throw RangeError("ringerTicks > 0xffff or < 1");
    }

    return [
        "",
        "    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;",
        "",
        "    ; halfPeriod = " + halfPeriod,
        "    ; cpuClockFrequency = " + cpuClockFrequency,
        "    ; prescalar = " + prescalar,
        "    ; timerFrequency (cpuClockFrequency / prescalar) = " +
            timerFrequency,
        "    ; tick (1000 / timerFrequency) = " + tick + " milliseconds",
        "    .equ ringerTicks = " + Math.round(ringerTicks)  +
            " ; halfPeriod / tick ($" + Math.round(ringerTicks).toString(16) + ")",
        "    .equ timer1Prescalar = " + prescalarBits(prescalar)
    ];
}
