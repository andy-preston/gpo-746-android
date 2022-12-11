    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"

    SetupOutputs
    BlinkOff
checkDial:
    SkipDialInactive
    rjmp active

    BlinkOff
    rjmp checkDial

active:
    BlinkOn
    rjmp checkDial
