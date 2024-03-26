    .device ATTiny2313
    .include "prelude.asm"
    .include "constants.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "test-delays.asm"
    .include "blinks.asm"
    .include "dial-counter.asm"

    ; Test the pulse counter and debounce circuit.
    ;
    ; Procedure:
    ;
    ; 1. With all dial pins attached and both dial outputs
    ;    from the switching board connected to the applicable inputs on the
    ;    microcontroller board. Dial a digit and once the dial is fully
    ;    returned the LED will "blink out" the count of the digit you dialled
    ;    (with "0" being 10)
    ;
    ; This test isn't quite a correct simulation because, as things stand
    ; `blink_count` will restart the timer at least once.

    setup_outputs
    setup_timer
    setup_dial
    cbi output_port, pin_out_LED

check_dial:
    get_dial_pulse_count
    tst _dialled_digit
    breq check_dial

    blink_count
    clr _dialled_digit
    rjmp check_dial