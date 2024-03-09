; This is stuff for counting pulses coming from the dial and grouping them
; into digits. For `skip_dial_active`, see `gpio.asm`


.macro setup_or_restart_dial
    clr _pulse_counter
    clr _dialled_digit
.endMacro


.macro get_dial_pulse_count
    skip_dial_inactive
    rjmp dial_active

dial_inactive:
    ; The pulse count might be 0 (indicating no digit dialed) or, if we've
    ; previously been active, it'll have a count of the pulses dialed and the
    ; digit is now finished.
    mov _dialled_digit, _pulse_counter
    ; We may or may not have been in these states.
    ; But, if the dial is inactive, we certainly don't want to be in them now.
    leave state_dial_active
    leave state_waiting_for_pulse_timer
    rjmp nothing_left_to_do

dial_active:
    skip_if_not_in state_dial_active
    rjmp already_active

only_just_gone_active:
    enter state_dial_active
    ; As we've only just entered the active state, there's no way that we can
    ; also have seen the start of a pulse and be waiting for the timer.
    leave state_waiting_for_pulse_timer
    ; Also, as we've only just gone active, there can't be any pulses counted
    ; yet either.
    setup_or_restart_dial

already_active:
    skip_if_pulse_is_low
    rjmp pulse_is_high

pulse_is_low:
    ; We're certainly not waiting for a pulse timer if it's already low.
    leave state_waiting_for_pulse_timer
    rjmp nothing_left_to_do

pulse_is_high:
    skip_if_in state_waiting_for_pulse_timer
    rjmp continue_waiting_for_pulse

start_waiting_for_pulse:
    start_interval_timer
    enter state_waiting_for_pulse_timer

continue_waiting_for_pulse:
    skip_if_interval_complete
    rjmp nothing_left_to_do

interval_complete:
    inc _pulse_counter
    ; Let's pretend that the previous pulse-pin state was LOW because it's got
    ; less that 20ms before it really does go low and the 30ms timer won't have
    ; completed again in that time.
    leave state_waiting_for_pulse_timer

nothing_left_to_do:
.endMacro
