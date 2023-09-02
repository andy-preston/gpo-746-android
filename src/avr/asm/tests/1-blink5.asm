    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "blinks.asm"

    SetupOutputs
    SetupTimer
seqStart:
    ldi _digit, 5
loop:
    BlinkCount
    rjmp seqStart
