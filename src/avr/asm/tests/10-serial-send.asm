    .device ATTiny2313
    .include "constants.asm"
    .include "prelude.asm"
    .include "timer.asm"
    .include "test-delays.asm"
    .include "serial.asm"
    .include "gpio.asm"
    .include "blinks.asm"

    ; Continuously sends the given string across the serial port which should be
    ; detected by the attached device.

    setup_timer
    setup_serial

the_top:
    ; lpm always uses the Z-Register and never X and Y
    ; It's OK for us to have a table in program memory here
    ; But, in "production", the Z-Register is used to hold our current
    ; position in the ring sequence and any other use of Z will clash with that.
    load_z_for_lpm digits_to_send
    blink_flip
send_digit:
    wait_for_20ms
    lpm _dialled_digit, Z+
    cpi _dialled_digit, ' '
    breq the_top
    write_serial
    rjmp send_digit

digits_to_send:
    .db "Testing1234 "