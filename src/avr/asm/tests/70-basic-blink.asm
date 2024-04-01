    .device ATmega164P
    .include "prelude.asm"
    .include "gpio.asm"

    ; Hopefully, you'll never need to run this one.
    ;
    ; I was having trouble getting the debounce working on the dial pulse
    ; reader and ended up building a special tester with an ATmega164P chip
    ; with it's two extra ports hooked up to a CY7C68013A for testing with
    ; Sigrok. This test is to do some preliminary testing on that board to make
    ; sure it will work.

    setup_outputs

top_of_test:

    ldi r25, 24

outer_loop:
    ldi r24, 255

middle_loop:
    ldi r23, 255

inner_loop:
    dec r23
    brne inner_loop
    dec r24
    brne middle_loop
    dec r25
    brne outer_loop

blink_check:
    sbic output_port, pin_out_LED
    rjmp blink_off

blink_on:
    sbi output_port, pin_out_LED
    rjmp top_of_test

blink_off:
    cbi output_port, pin_out_LED
    rjmp top_of_test
