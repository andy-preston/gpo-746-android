    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "attiny/modules/registers.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/prescale.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/ring.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
    SetupTimer
testLoop:
    Ringing
    TestDelay 0x40
    rjmp testLoop

ringSequence:
    RingData emulatedRing