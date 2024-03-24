    .include "constants.asm"
    .include "prelude.asm"
    .include "timer.asm"
    .include "test-delays.asm"
    .include "serial.asm"
    .include "gpio.asm"
    .include "blinks.asm"

    ; A complete fake dialing sequence with a valid number for testing the
    ; android app more than the microcontroller code.

    setup_outputs
    setup_timer
    setup_serial

the_top:
    cbi output_port, pin_out_pick_up_RI

    wait_for_five_seconds
    wait_for_five_seconds

    sbi output_port, pin_out_pick_up_RI

    ; lpm always uses the Z-Register and never X and Y
    ; It's OK for us to have a table in program memory here
    ; But, in "production", the Z-Register is used to hold our current
    ; position in the ring sequence and any other use of Z will clash with that.
    load_z_for_lpm digits_to_send

send_digit:
    lpm _dialled_digit, Z+
    cpi _dialled_digit, ' '
    breq number_finished
    write_serial

    blink_flip
    wait_for_half_a_second
    rjmp send_digit

number_finished:
    wait_for_five_seconds
    wait_for_five_seconds
    rjmp the_top

digits_to_send:
    .db "0800567890  "
