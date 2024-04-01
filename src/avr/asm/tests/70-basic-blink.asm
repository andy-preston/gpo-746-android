    .device ATmega164P
    .include "prelude.asm"
    .include "constants.asm"
    .include "gpio.asm"

    ; Hopefully, you'll never need to run this one.
    ;
    ; I was having trouble getting the debounce working on the dial pulse
    ; reader and ended up building a special tester with an ATmega164P chip
    ; with it's two extra ports hooked up to a CY7C68013A for testing with
    ; Sigrok. This test is to do some preliminary testing on that board to make
    ; sure it will work.

    .equ ring_OCF1A = OCF1A
    .equ debounce_OCF1B = OCF1B

    setup_outputs

    sts TCCR1A, _zero

    ldi _io, timer1_clock_select
    sts TCCR1B, _io

    ldi _io, high(timer1_ring_ticks)
    sts OCR1AH, _io

    ldi _io, low(timer1_ring_ticks)
    sts OCR1AL, _io

    ldi _io, high(timer1_debounce_ticks)
    sts OCR1BH, _io

    ldi _io, low(timer1_debounce_ticks)
    sts OCR1BL, _io

top_of_test:

    ldi _delay_repeat, 40

delay:
    sts TCNT1H, _zero
    sts TCNT1L, _zero
    ldi _io, (1 << ring_OCF1A) | (1 << debounce_OCF1B)
    out TIFR1, _io

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
