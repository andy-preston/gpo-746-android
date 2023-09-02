    .include "prelude.asm"
    .include "gpio.asm"

    SetupOutputs
checkHook:
    SkipOffHook
    rjmp onHook

    BlinkOn
    rjmp checkHook

onHook:
    BlinkOff
    rjmp checkHook
