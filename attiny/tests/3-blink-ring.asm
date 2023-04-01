    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/precompiled.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/ring.asm"

    SetupOutputs
    SetupTimer
testLoop:
    Ringing
    rjmp testLoop

ringSequence:
    RingData emulatedRing