    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "lib/registers.asm"
    .include "lib/gpio.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
    BlinkOff
checkDial:
    SkipDialActive
    rjmp inactive

    BlinkOn
    rjmp checkDial

inactive:
    BlinkOff
    rjmp checkDial
