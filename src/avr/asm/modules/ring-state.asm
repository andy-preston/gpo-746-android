; Do this once as we transition to the ringing state
.macro start_ringing
    start_20ms_wait
    setup_ring_sequence
.endMacro

; When the phone is in the "ringing" state this macro should run once
; every time round the loop
.macro ring_sequence_step
    load_ring_sequence_byte
    complete_20ms_wait
    output_ring_sequence_byte
    start_20ms_wait
.endMacro
