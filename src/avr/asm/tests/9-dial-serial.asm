    .include "prelude.asm"
    .include "gpio.asm"
    .include "dial-counter.asm"
    .include "serial.asm"
    .include "dial-serial.asm"

    ; Dial multiple digits and they should be sent across the serial device for
    ; detected by the attached device.

    setup_outputs
    setup_dial
    setup_serial

check_dial:
    get_and_send_digit
    rjmp check_dial
