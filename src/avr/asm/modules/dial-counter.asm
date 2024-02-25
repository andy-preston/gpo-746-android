; This is stuff for counting pulses coming from the dial and grouping them
; into digits. For `skip_dial_inactive`, see `gpio.asm`


.macro setup_dial
    clr _pulse_counter
    clr _dialled_digit
.endMacro


.macro get_dial_pulse_count
    skip_dial_active
    rjmp dial_inactive

dial_active:
    skip_if_not_in state_dial_active
    rjmp keep_counting_pulses

start_counting_pulses:
    enter state_dial_active
    leave state_previous_pulse_high

keep_counting_pulses:
    skip_if_pulse_is_high
    rjmp end

pulse_is_high:
    skip_if_in state_previous_pulse_high
    rjmp keep_waiting_for_pulse

start_waiting_for_pulse:
    start_interval_timer

keep_waiting_for_pulse:
    skip_if_interval_complete
    rjmp end

interval_complete:
    inc _pulse_counter
    ; Let's pretend that the previous pulse-pin state was LOW because it's got
    ; less that 20ms before it really does go low and the 30ms timer won't have
    ; completed again in that time.
    leave state_previous_pulse_high
    rjmp end

dial_inactive:
    ; Return a digit to the next step if any pulses were counted, otherwise
    ; return zero which indicates "no digital dialled (yet)"
    mov _dialled_digit, _pulse_counter
    leave state_dial_active

end:
.endMacro
