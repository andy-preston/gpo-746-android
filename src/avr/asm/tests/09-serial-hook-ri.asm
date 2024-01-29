    .include "prelude.asm"
    .include "gpio.asm"
    .include "ring-timer.asm"

    ; Will send a high RI signal for approximately 400ms and then a low RI
    ; signal for approximately 400ms, the remote serial device should be able
    ; to detect this.

    setup_outputs
    setup_20ms_timer

the_top:
    blink_on
    send_picked_up_signal
    wait_for_multiple_20ms 25

    blink_off
    send_put_down_signal
    wait_for_multiple_20ms 25

    rjmp the_top