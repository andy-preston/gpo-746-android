    ; Bits of _state_flags to represent where teh state machine currently is
    ; previously was.

    .equ state_ringing = 0
    .equ state_dial_active = 1
    .equ state_waiting_for_pulse_timer = 2


.macro setup_state_machine
    ; The state machine is in a zero state right at the start
    clr _state_flags
.endMacro


.macro enter
    sbr _state_flags, @0
.endMacro


.macro leave
    cbr _state_flags, @0
.endMacro


.macro skip_if_not_in
    sbrc _state_flags, @0
.endMacro


.macro skip_if_in
    sbrs _state_flags, @0
.endMacro
