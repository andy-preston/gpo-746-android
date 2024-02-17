; This is stuff for counting pulses coming from the dial and grouping them
; into digits. For `skip_dial_inactive`, see `gpio.asm`

.macro setup_dial
    ; previously to the beginning of time - the pulse pin was low
    clr _previous_pulse_state
    ; And we can't have counted any pulses yet - because time hasn't started yet!
    clr _pulse_counter
    ; We skip sending a digit if it's zero, so this is a sensible default
    clr _dialled_digit
.endMacro


.macro accumulate_pulse_count
    ; If the pulse pin is low then there's nothing to do except save the
    ; previous state.
    in _io, input_port
    sbrs _io, pin_in_dial_pink
    rjmp save_previous_state

pulse_is_high:
    ; But if it is high we ensure that it's been consistently high for
    ; at least 30ms (it's a 50ms pulse).
    sbrs _previous_pulse_state, pin_in_dial_pink
    rjmp pulse_was_previously_low:

pulse_was_previously_high:
    ; If it been high for long enough, it counts as a pulse.
    skip_if_interval_complete
    rjmp save_previous_state

been_high_for_long_enough:
    inc _pulse_counter
    ; Let's pretend that the previous pulse-pin state was LOW because it's got
    ; less that 20ms before it really does go low and the 30ms timer won't have
    ; completed again in that time.
    clr _io
    rjmp save_previous_state

pulse_was_previously_low:
    ; This is the transition of a low to high. So let's start the timer to
    ; make sure it stays consistently high.
    start_30ms_timer

save_previous_state:
    mov _previous_pulse_state, _io
.endMacro


.macro get_dial_pulse_count
    ; If the dial is active then the full pulse count is not yet available
    ; but we should accumulate the next value.
    skip_dial_active
    rjmp dial_inactive

dial_active:
    accumulate_pulse_count
    rjmp end_of_pulse

dial_inactive:
    ; There may be a digit available now, or perhaps nothing's happened and
    ; it's zero.
    mov _dialled_digit, _pulse_counter

    ; If it's zero, again, we've got nothing to do.
    tst _dialled_digit
    breq end_of_pulse_count

    ; If it's not zero, clear the counter ready for the next one.
    clr _pulse_counter

end_of_pulse_count:
.endMacro
