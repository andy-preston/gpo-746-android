    .include "prelude.asm"
    .include "gpio.asm"

    ; Test the hook switch, pick up the phone and the LED should light,
    ; put it down again and the LED should go out.

    setup_outputs
check_hook:
    skip_instruction_when_off_hook
    rjmp on_the_hook

    blink_on
    rjmp check_hook

on_the_hook:
    blink_off
    rjmp check_hook
