    .include "prelude.asm"
    .include "gpio.asm"

    ; Test the "dialing in progress" switch in the dial.
    ;
    ; Procedure:
    ;
    ; 1. Using just the microcontroller board (with it's power jumper shorted),
    ;    short `pin_in_dial_grey` to VCC and the LED will light. Short
    ;    `pin_in_dial_grey` to ground and the LED will go out.
    ;
    ; 2. Using the switching board attached (with it's power jumper missing),
    ;    short the "grey" pin to VCC and the LED will light.
    ;
    ; 3. With "grey" and and "blue" on the dial attached, dial a digit and as
    ; the dial is in motion the LED will light.

    setup_outputs
    blink_off
check_dial:
    skip_dial_inactive
    rjmp active

    blink_off
    rjmp check_dial

active:
    blink_on
    rjmp check_dial
