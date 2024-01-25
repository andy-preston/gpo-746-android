    .include "prelude.asm"
    .include "gpio.asm"
    .include "ring-timer.asm"

    ; A Very simple test which will switch the amp on and off with a 3 second
    ; delay.

    setup_outputs
    setup_20ms_timer
loop:
    switch_amp_on
    wait_for_three_seconds
    switch_amp_off
    wait_for_three_seconds
    rjmp loop
