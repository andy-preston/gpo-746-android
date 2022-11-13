    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "lib/registers.asm"
    .include "lib/gpio.asm"
    .include "lib/prescale.asm"
    .include "lib/timer.asm"
    .include "lib/blinks.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
    SetupTimer
seqStart:
    ldi _count, 5
loop:
    BlinkCount
    rjmp seqStart
