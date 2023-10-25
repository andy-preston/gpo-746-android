    .include "prelude.asm"
    .include "gpio.asm"

    ; Test the hook switch, pick up the phone and the LED should light,
    ; put it down again and the LED should go out.

    SetupOutputs
checkHook:
    SkipOffHook
    rjmp onHook

    BlinkOn
    rjmp checkHook

onHook:
    BlinkOff
    rjmp checkHook
