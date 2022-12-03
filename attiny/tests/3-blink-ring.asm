    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "modules/registers.asm"
    .include "modules/gpio.asm"
    .include "modules/prescale.asm"
    .include "modules/timer.asm"
    .include "modules/ring.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
    SetupTimer
testLoop:
    Ringing
    TestDelay 0x40
    rjmp testLoop

    .equ ding = 1 << pinBlink
    .equ dong = 0
ringSequence:
    RingData