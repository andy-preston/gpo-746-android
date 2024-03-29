    .device ATmega644P
    .include "prelude.asm"
    .include "constants.asm"
    .include "gpio.asm"
    .include "timer.asm"

    ; Hopefully, you'll never need to run this one.
    ;
    ; I was having trouble getting the debounce working on the dial pulse
    ; reader and ended up building a special tester with an ATMega644P chip
    ; with it's two extra ports hooked up to a CY7C68013A for testing with
    ; Sigrok. This test is to do some preliminary testing on that board to make
    ; sure it will work.

    setup_outputs
    setup_timer
    mov _bounce_state, _zero
    mov _pulse_count, _all_bits_high

test_loop:
    in _timer_wait, TIFR
    sbrc _timer_wait, debounce_interval
    rjmp test_loop

timer_tick:
    out PORTA, _bounce_state
    out PORTC, _pulse_count
    inc _bounce_state
    dec _pulse_count
    start_interval_timers
    rjmp test_loop
