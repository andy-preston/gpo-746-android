    .device ATTiny2313
    .include "constants.asm"
    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "ring-sequence.asm"

    ; Test the blinkenlicht and the ring sequence.
    ;
    ; Procedure:
    ;
    ; 1. Using just the microcontroller board (with it's power jumper shorted)
    ;    The LED should blink "impersonating" the ringing cadence of one of
    ;    the bells.

    setup_outputs
    setup_timer
    ring_sequence_start
testLoop:
    ring_sequence_step
    rjmp testLoop

ring_sequence:
    ring_data emulated_ring