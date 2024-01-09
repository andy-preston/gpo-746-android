    .include "prelude.asm"
    .include "gpio.asm"
    .include "ring-timer.asm"
    .include "blinks.asm"

    ; A Very simple test which will blink the LED 5 times with a 200ms delay,
    ; wait 500ms and blink it 5 times again... ad infinitum.

    setup_outputs
    setup_20ms_timer
    ldi _dialled_digit, 5
loop:
    blink_count
    rjmp loop
