    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "modules/registers.asm"
    .include "modules/gpio.asm"
    .include "modules/prescale.asm"
    .include "modules/timer.asm"
    .include "modules/blinks.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
    SetupTimer
seqStart:
    ldi _count, 5
loop:
    BlinkCount
    rjmp seqStart
