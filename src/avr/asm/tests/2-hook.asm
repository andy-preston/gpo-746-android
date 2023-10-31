    .include "prelude.asm"
    .include "gpio.asm"

    ; Test the receiver state switch, pick up the phone and the LED should
    ; light, put it down again and the LED should go out.

    setup_outputs
check_receiver_state:
    skip_when_picked_up
    rjmp receiver_down

    blink_on
    rjmp check_receiver_state

receiver_down:
    blink_off
    rjmp check_receiver_state
