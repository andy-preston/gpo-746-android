    .include "prelude.asm"
    .include "gpio.asm"

    ; Dial a digit and as the dial is returning the LED should light.

    setup_outputs
    blink_off
check_dial:
    skip_instruction_dial_inactive
    rjmp active

    blink_off
    rjmp check_dial

active:
    blink_on
    rjmp check_dial
