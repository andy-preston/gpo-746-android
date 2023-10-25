    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "ring.asm"

    ; Test the ringing without the bells installed - the LED should
    ; "impersonate" the ringing cadence of one of the bells.

    SetupOutputs
    Setup20msTimer
testLoop:
    Ringing
    rjmp testLoop

ringSequence:
    RingData emulatedRing