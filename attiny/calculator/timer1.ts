export default function(cpuClockFrequency: number): string {

    const /* TCCR1B */ prescalerBits = function(prescaler: number) {
        return {
            0: "(1 << CS10)",
            8: "(1 << CS11)",
            64: "(1 << CS11) | (1 << CS10)",
            256: "(1 << CS12)",
            1024: "(1 << CS12) | (1 << CS10)"
        }[prescaler];
    }

    const halfPeriod = 20;
    const prescaler = 256;
    const timerFrequency: number = cpuClockFrequency / prescaler;
    const tick: number = (1 / timerFrequency) * 1000;
    const ringerTicks: number = halfPeriod / tick;
    if (ringerTicks > 0xffff || ringerTicks < 1) {
        throw RangeError("ringerTicks > 0xffff or < 1");
    }
    return `.equ timer1Prescaler = ${prescalerBits(prescaler)}`;
}
