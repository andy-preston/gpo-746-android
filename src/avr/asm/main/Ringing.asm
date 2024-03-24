.macro Ringing
    sbis input_pins, pin_in_incoming_RTS
    rjmp no_incoming

    skip_if_not_in state_ringing
    rjmp step

    ring_sequence_start

step:
    enter state_ringing
    ring_sequence_step
    rjmp end

no_incoming:
    leave state_ringing
end:
.endMacro
