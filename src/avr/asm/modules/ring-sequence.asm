; We mark end of data with an output byte with a bit set for a pin that
; doesn't even exist.
.equ end_of_sequence_data = 1 << pin_out_absent_B6


.macro setup_ring_sequence
    ; As this stands, we're counting on no other code using the Z register pair.
    ; This would certainly not be the case if some other routine wanted to `lpm`.
    ; But for now we're safe in that assumption.
    load_z_for_lpm ring_sequence
.endMacro


.macro load_ring_sequence_byte
    lpm _ring_sequence_byte, Z+
    cpi _ring_sequence_byte, end_of_sequence_data
    brne not_at_end_of_data

    load_z_for_lpm ring_sequence
    lpm _ring_sequence_byte, Z+
not_at_end_of_data:
.endMacro


.macro output_ring_sequence_byte
    ; This will also clear all other output bits which includes the signal
    ; to the android device that the receiver has been picked up (the phone is
    ; off-hook).
    ; I don't anticipate that causing any problems as the phone shouldn't be
    ; ringing once the off-shook signal is being sent.
    out output_port, _ring_sequence_byte
.endMacro


; Calling `ring_data emulated_ring` will load a sequence that will flash the
; diagnostic LED instead of actually ringing the bells. Used for testing when
; "analogue" board is not plugged in.
; `ring_data real_ring` should be used in "production" code.

; It's not important but it probably makes it easier if the ring_data is after
; all the actual code in the program memory.

.equ emulated_ring = 1
.equ real_ring = 2
.macro ring_data
    .if @0 == emulated_ring
        ; For testing purposes instead of outputting to the bell pins
        ; output the "ding" pulses to the diagnostic LED instead and
        ; set the "dong" pulses to 0 so that they'll be ignored
        .equ ding = 1 << pin_out_LED
        .equ dong = 0
    .else
        .equ ding = 1 << pin_out_ding
        .equ dong = 1 << pin_out_dong
    .endIf

    ; Original GPO phones have the ringer solenoids wired in such a way that
    ; they respond to AC pulses at 25Hz (half-period = 20mS).
    ; For this system, they have been wired separately to an IO pin and to
    ; ground so that they can easily be driven digitally.
    ; The traditional British ring cadence is
    ; 400mS ring, 200mS silence, 400mS ring, 2000mS silence (3 second total)
    .db ding, dong, ding, dong, ding, dong, ding, dong, ding, dong ; 20X20
    .db ding, dong, ding, dong, ding, dong, ding, dong, ding, dong ;      =400ms
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0 ; 10X20=200ms
    .db ding, dong, ding, dong, ding, dong, ding, dong, ding, dong ; 20X20
    .db ding, dong, ding, dong, ding, dong, ding, dong, ding, dong ;      =400ms
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0 ; 100X20
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0 ;     =2000ms
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db end_of_sequence_data, end_of_sequence_data
.endMacro
