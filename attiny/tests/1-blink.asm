    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "lib/registers.asm"
    .include "lib/gpio.asm"
    .include "lib/prescale.asm"
    .include "lib/timer.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
    SetupTimer
seqStart:
    ldi _count, 0x20
loop:
    Blink
    TestDelay _count
    dec _count
    brne loop
    rjmp seqStart
