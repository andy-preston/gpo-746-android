    .include "prelude.asm"
    .include "gpio.asm"
    .include "ring-timer.asm"

    ; A Very simple test which will switch the amp on and off with a 3 second
    ; delay.

    ; Procedure:
    ;
    ; 1. Attach a multimeter to `pin_out_amplifier` on the bare microcontroller
    ;    board and check that the voltage goes high as expected
    ;
    ; 2. Connect the amplifier board but without the LM386 inserted and
    ;    check with a multimeter that the transistor is switching VDD.
    ;
    ; 3. Insert the LM386 into the amplifier board and ensure that the
    ;    chip is powering up.
    ;    Can this be done by measuring the voltage on pin 5?

    setup_outputs
    setup_20ms_timer
loop:
    switch_amp_on
    wait_for_three_seconds
    switch_amp_off
    wait_for_three_seconds
    rjmp loop
