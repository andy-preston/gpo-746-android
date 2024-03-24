    .include "constants.asm"
    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "state-machine.asm"
    .include "ring-sequence.asm"
    .include "dial-counter.asm"
    .include "dial-ascii.asm"
    .include "serial.asm"

    .include "Amplifier.asm"
    .include "Hook_And_Dial.asm"
    .include "Ringing.asm"

    setup_outputs
    setup_timer
    setup_state_machine
    setup_dial
    setup_ascii
    setup_serial

loop:
    Ringing
    Hook_And_Dial
    Amplifier
    rjmp loop

ring_sequence:
    ring_data real_ring
