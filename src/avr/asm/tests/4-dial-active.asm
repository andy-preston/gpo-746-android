    .include "prelude.asm"
    .include "gpio.asm"

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
