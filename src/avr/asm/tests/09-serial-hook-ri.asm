    .device ATTiny2313
    .include "constants.asm"
    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "test-delays.asm"

    ; Will send a high RI signal for approximately 400ms and then a low RI
    ; signal for approximately 400ms, the remote serial device should be able
    ; to detect this.

    setup_outputs
    setup_timer

the_top:
    sbi output_port, pin_out_LED
    sbi output_port, pin_out_pick_up_RI
    wait_for_half_a_second

    cbi output_port, pin_out_LED
    cbi output_port, pin_out_pick_up_RI
    wait_for_half_a_second

    rjmp the_top
