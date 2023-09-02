    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "ring.asm"

    SetupOutputs
    SetupTimer
testLoop:
    Ringing
    rjmp testLoop

ringSequence:
    RingData emulatedRing