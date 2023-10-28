    .include "prelude.asm"
    .include "gpio.asm"
    .include "ring-timer.asm"
    .include "ring-sequence.asm"
    .include "ring-state.asm"
    .include "dial-counter.asm"
    .include "dial-ascii.asm"
    .include "serial.asm"

    setup_outputs
    setup_20ms_timer
    setup_dial
    setup_serial

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

enter_waiting:
state_waiting:
    skip_instruction_on_no_incoming
    rjmp enter_ringing

    skip_instruction_when_off_hook
    rjmp enter_dialing

    rjmp state_waiting

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

enter_ringing:
    start_ringing
state_ringing:
    ring_sequence_step
    skip_instruction_when_off_hook
    rjmp state_ringing
    ; otherwise "fall through" straight into enter_calling

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

enter_calling:
state_calling:
    skip_instruction_when_off_hook
    rjmp enter_waiting

    rjmp state_calling

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

enter_dialing:
    reset_or_abort_dialing
state_dialing:
    ; This is a peculiar state that never happens with a real phone.
    ; The hook is off, the user is dialing and a call comes in
    ; On a real phone, the line would be engaged at this point.
    ; I'm not sure if it's possible to make an Android phone "believe" it's
    ; engaged when it's, technically, not so.
    skip_instruction_on_no_incoming
    rjmp enter_calling

    skip_instruction_when_off_hook
    rjmp abandon_dialing

    get_dial_pulse_count
    ; If there are no pulses, then we can just skip to the end
    tst _dialled_digit
    breq state_dialing
    ; ... otherwise
    convert_pulse_count_to_ascii
    write_serial
    rjmp state_dialing

abandon_dialing:
    reset_or_abort_dialing
    rjmp enter_waiting

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

ring_sequence:
    ring_data real_ring
