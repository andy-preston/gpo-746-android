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
    ; 2. Attach the switching board (with it's power jumper missing), by
    ;    connecting it's dial active output to `pin_in_dial_grey`
    ;    short the "grey" pin of the switching board's dial input to VCC and
    ;    the LED will light.
    ;
    ; 3. With "grey" and and "blue" of the dial attached to the switching
    ;    board's inputs, dial a digit and when the dial is in motion the LED
    ;    will light.

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
