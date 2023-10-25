    .include "prelude.asm"
    .include "gpio.asm"

    ; Dial a digit and as the dial is returning the LED should light.

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
