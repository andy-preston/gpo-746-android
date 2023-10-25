    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "ring.asm"

    SetupOutputs
    Setup20msTimer
testLoop:
    Ringing
    rjmp testLoop

ringSequence:
    RingData emulatedRing