    .include "prelude.asm"
    .include "gpio.asm"
    .include "ring-timer.asm"
    .include "ring-sequence.asm"
    .include "ring-state.asm"

    ; Test the ringing without the bells installed - the LED should
    ; "impersonate" the ringing cadence of one of the bells.

    setup_outputs
    setup_20ms_timer
    start_ringing
testLoop:
    ring_sequence_step
    rjmp testLoop

ring_sequence:
    ring_data emulated_ring