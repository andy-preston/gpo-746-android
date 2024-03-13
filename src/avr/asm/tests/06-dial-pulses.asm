    .include "constants.asm"
    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "state-machine.asm"
    .include "dial-counter.asm"
    .include "blinks.asm"

    ; Test the pulse counter and debounce circuit.
    ;
    ; Procedure:
    ;
    ; 1. Insert the 555 and connect it's output to a 'scope or logic analyzer
    ;    ensure there is one pulse emitted at about 50ms each.
    ;
    ; 2. With the 555 inserted and all dial pins attached and both dial outputs
    ;    from the switching board connected to the applicable inputs on the
    ;    microcontroller board. Dial a digit and once the dial is fully
    ;    returned the LED will "blink out" the count of the digit you dialled
    ;    (with "0" being 10)

    setup_outputs
    setup_timer
    setup_state_machine
    setup_or_restart_dial
    blink_off
check_dial:
    different_pulse_count
    tst _dialled_digit
    breq check_dial

    blink_count
    clr _dialled_digit
    rjmp check_dial
