; Do this once as we transition to the ringing state
.macro start_ringing
    set_timer_interval_to_20ms
    start_interval_timer
    setup_ring_sequence
.endMacro

; When the phone is in the "ringing" state this macro should run once
; every time round the loop
.macro ring_sequence_step
    load_ring_sequence_byte
    wait_for_interval
    output_ring_sequence_byte
    start_interval_timer
.endMacro
