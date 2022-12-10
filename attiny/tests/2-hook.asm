    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "attiny/modules/registers.asm"
    .include "attiny/modules/gpio.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
checkHook:
    SkipOffHook
    rjmp onHook

    BlinkOn
    rjmp checkHook

onHook:
    BlinkOff
    rjmp checkHook
