    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "modules/registers.asm"
    .include "modules/gpio.asm"

progStart:
    SetupStackAndReg
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
