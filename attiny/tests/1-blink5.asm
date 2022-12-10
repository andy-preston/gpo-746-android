    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "attiny/modules/registers.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/prescale.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/blinks.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
    SetupTimer
seqStart:
    ldi _digit, 5
loop:
    BlinkCount
    rjmp seqStart
