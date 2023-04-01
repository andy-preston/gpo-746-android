export default function(cpuClockFrequency : number) {

    const /* TCCR1B */ prescalerBits = function(prescaler : number) {
        return {
            0: "(1 << CS10)",
            8: "(1 << CS11)",
            64: "(1 << CS11) | (1 << CS10)",
            256: "(1 << CS12)",
            1024: "(1 << CS12) | (1 << CS10)"
        }[prescaler];
    }

    const halfPeriod : number = 20;
    const prescaler : number = 256;
    const timerFrequency : number = prescaler == 0 ?
        cpuClockFrequency : cpuClockFrequency / prescaler;
    const tick : number = (1 / timerFrequency) * 1000;
    const ringerTicks : number = halfPeriod / tick;
    if (ringerTicks > 0xffff || ringerTicks < 1) {
        throw RangeError("ringerTicks > 0xffff or < 1");
    }
    return [
        "",
        "    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;",
        "",
        "    ; halfPeriod = " + halfPeriod,
        "    ; cpuClockFrequency = " + cpuClockFrequency,
        "    ; prescaler = " + prescaler,
        "    ; timerFrequency (cpuClockFrequency / prescaler) = " +
            timerFrequency,
        "    ; tick (1000 / timerFrequency) = " + tick + " milliseconds",
        "    .equ ringerTicks = " + Math.round(ringerTicks)  +
            " ; halfPeriod / tick ($" + Math.round(ringerTicks).toString(16) + ")",
        "    .equ timer1Prescaler = " + prescalerBits(prescaler)
    ];
}
