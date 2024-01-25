    .include "prelude.asm"
    .include "ring-timer.asm"
    .include "serial.asm"
    .include "gpio.asm"
    .include "blinks.asm"

    ; A complete fake dialing sequence with a valid number for testing the
    ; android app more than the microcontroller code.

    setup_outputs
    setup_20ms_timer
    setup_serial

the_top:
    send_put_down_signal

    ldi _long_delay, 20
wait_to_settle:
    blink_flip
    wait_for_a_second
    dec _long_delay
    brne wait_to_settle

    send_picked_up_signal

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
    wait_for_multiple_20ms 25
    rjmp send_digit

number_finished:
    ldi _long_delay, 10
wait_to_restart:
    blink_flip
    wait_for_a_second
    dec _long_delay
    brne wait_to_restart

    rjmp the_top

digits_to_send:
    .db "0800567890  "
