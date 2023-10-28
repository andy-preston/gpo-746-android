    .include "prelude.asm"
    .include "gpio.asm"

    ; Test the hook switch, pick up the phone and the LED should light,
    ; put it down again and the LED should go out.

    setup_outputs
checkHook:
    skip_instruction_when_off_hook
    rjmp onHook

    blink_on
    rjmp checkHook

onHook:
    blink_off
    rjmp checkHook
