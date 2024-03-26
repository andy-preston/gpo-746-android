    .device ATTiny2313
    .include "prelude.asm"
    .include "gpio.asm"

    ; Test the receiver state switch.
    ;
    ; Procedure:
    ;
    ; 1. Using just the microcontroller board (with it's power jumper shorted),
    ;    short pin `pin_in_receiver` to ground and the LED will light.
    ;
    ; 2. Attach the switching board (with it's power jumper missing)
    ;    connecting it's hook output to `pin_in_receiver`. Then short the
    ;    switching board's hook input pins and the LED will light.
    ;
    ; 3. With the telephone hook attached, The light will be on by default and
    ;    picking up the phone will cause the LED to go off.

    setup_outputs
check_receiver_state:
    sbis input_pins, pin_in_receiver
    rjmp receiver_down

    cbi output_port, pin_out_LED
    rjmp check_receiver_state

receiver_down:
    sbi output_port, pin_out_LED
    rjmp check_receiver_state
