; This is the stuff that deals with sending dialled digits down the serial link.
; For `get_dial_pulse_count`, see `./dial-counter.asm`
; For `write_serial`, see `./serial.asm`


.macro convert_pulse_count_to_ascii
    ; Convert the pulse count in `_dialled_digit` to an ASCII representation.

    ; If digit is zero, ignore it
    tst _dialled_digit
    breq end_ascii_count

    ; If there were 10 pulses, the digit "0" has been dialled
    cpi _dialled_digit, 10

    ; If it's NOT 10 pulses, skip to the end - the number of pulses is the
    ; digit dialled
    brne digit_found

    ; But it it was 10 pulses, clear _dialled_digit so that it's the digit "0"
    clr _dialled_digit

digit_found:
    ; Convert the digit to an ASCII character by adding the value of ASCII "0"
    ; We could achieve the same effect by using a logical OR...
    add _dialled_digit, _ascii_zero
end_ascii_count:
.endMacro
