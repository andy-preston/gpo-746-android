; This is the stuff that deals with sending dialled digits down the serial link.
; For `get_dial_pulse_count`, see `./dial-counter.asm`
; For `write_serial`, see `./serial.asm`


.macro convert_pulse_count_to_ascii
    ; This is only used in `GetAndsend_digit`
    ; It's responsible for converting the pulse count in `_dialled_digit`
    ; to an ASCII representation.

    ; If digit is zer0, ignore it
    tst _dialled_digit
    breq end_ascii_count

    ; If there were 10 pulses, the digit "0" has been dialled
    cpi _dialled_digit, 10

    ; If it's not 10 pulses, skip to the end - the number of pulses is the
    ; digit dialled
    brne digit_found

    ; But it it was 10 pulses, clear _dialled_digit so that it;s the digit "0"
    clr _dialled_digit

digit_found:
    ; Convert the digit to an ASCII character by adding the value of ASCII "0"
    ; We could achieve the same effect by using a logical OR...
    add _dialled_digit, _ascii_zero
end_ascii_count:
.endMacro


.macro get_and_send_digit
    ; Reads the dial. If there are any pulses, convert the number to ASCII and
    ; write it to the serial link

    get_dial_pulse_count

    ; If there are no pulses, then we can just skip to the end
    tst _dialled_digit
    breq nothing_to_send

    ; But if there is a digit, convert it to ASCII
    ; and send it to the serial port
    convert_pulse_count_to_ascii
    write_serial
nothing_to_send:
.endMacro
