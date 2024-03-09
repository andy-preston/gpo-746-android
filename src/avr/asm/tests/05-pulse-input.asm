    .include "prelude.asm"
    .include "gpio.asm"
    .include "blinks.asm"

    ; Test the pulse input without the dial attached.
    ;
    ; Procedure:
    ;
    ; 1. Using just the microcontroller board (with it's power jumper shorted),
    ;    short `pin_in_dial_pink` to VCC and the LED will light. Short
    ;    `pin_in_dial_pink` to ground and the LED will go out.
    ;
    ; 2. Attach the switching board (with it's power jumper missing and 555 not
    ;    inserted), with it's "dial pulse" output connected to
    ;    `pin_in_dial_pink`. Short pin 3 of the 555 to VCC and the LED will
    ;    light. Short pin 3 to ground and the LED will go out.

    setup_outputs
    blink_off
check_pulse:
    skip_if_pulse_is_low
    rjmp it_is_high

it_is_low:
    blink_off
    rjmp check_pulse

it_is_high:
    blink_on
    rjmp check_pulse
