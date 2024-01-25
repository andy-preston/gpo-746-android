    .include "prelude.asm"
    .include "gpio.asm"
    .include "ring-timer.asm"
    .include "dial-counter.asm"
    .include "blinks.asm"

    ; Dial a digit and once the dial is fully returned the LED should flash
    ; ONCE the for the count of the digit you dialled (with "0" being 10)

    setup_outputs
    setup_20ms_timer
    setup_dial
    blink_off
check_dial:
    get_dial_pulse_count
    tst _dialled_digit
    breq check_dial

    blink_count
    rjmp check_dial
