    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"

    SetupOutputs
checkHook:
    SkipOffHook
    rjmp onHook

    BlinkOn
    rjmp checkHook

onHook:
    BlinkOff
    rjmp checkHook
