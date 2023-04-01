    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/timer1_prescaler.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/blinks.asm"

    SetupOutputs
    SetupTimer
seqStart:
    ldi _digit, 5
loop:
    BlinkCount
    rjmp seqStart
