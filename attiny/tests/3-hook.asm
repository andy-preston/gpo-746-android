    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "lib/registers.asm"
    .include "lib/gpio.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
checkHook:
    SkipOnHook
    rjmp offHook

    BlinkOn
    rjmp checkHook

offHook:
    BlinkOff
    rjmp checkHook
