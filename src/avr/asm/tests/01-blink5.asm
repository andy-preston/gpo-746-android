    .include "constants.asm"
    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "test-delays.asm"
    .include "blinks.asm"

    ; Test the blinkenlicht
    ;
    ; Procedure:
    ;
    ; 1. Using just the microcontroller board (with it's power jumper shorted)
    ;    The LED will blink 5 times with a 200ms delay, wait 500ms and blink
    ;    5 times again... ad infinitum.

    setup_outputs
    setup_timer

    ldi _dialled_digit, 5
loop:
    blink_count
    rjmp loop
