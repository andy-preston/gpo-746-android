    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "state-machine.asm"
    .include "dial-counter.asm"
    .include "serial.asm"
    .include "dial-ascii.asm"

    ; Dial multiple digits and they should be sent across the serial device for
    ; detected by the attached device.

    setup_outputs
    setup_state_machine
    setup_dial
    setup_ascii
    setup_serial
    set_timer_interval_to_30ms

check_dial:
    get_dial_pulse_count
    ; If there are no pulses, then we can just skip to the end
    tst _dialled_digit
    breq check_dial
    ; But if there is a digit, convert it to ASCII
    ; and send it to the serial port
    convert_pulse_count_to_ascii
    write_serial
    rjmp check_dial
