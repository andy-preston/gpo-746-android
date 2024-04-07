    .device ATmega164P
    .include "prelude.asm"
    .include "constants.asm"
    .include "gpio.asm"
    .include "timer.asm"

    ; Hopefully, you'll never need to run this one.
    ;
    ; I was having trouble getting the debounce working on the dial pulse
    ; reader and ended up building a special tester with an ATmega164P chip
    ; with it's two extra ports hooked up to a CY7C68013A for testing with
    ; Sigrok. This test is to do some preliminary testing on that board to make
    ; sure it will work.

    setup_outputs
    setup_timer

top_of_test:

    ldi _delay_repeat, 40

delay:
    start_interval_timers

wait_for_timer:
    in _timer_wait, TIFR1
    sbrs _timer_wait, debounce_OCF1B
    rjmp wait_for_timer

    dec _delay_repeat
    brne delay

blink_check:
    sbic output_port, pin_out_LED
    rjmp blink_off

blink_on:
    sbi output_port, pin_out_LED
    rjmp top_of_test

blink_off:
    cbi output_port, pin_out_LED
    rjmp top_of_test
