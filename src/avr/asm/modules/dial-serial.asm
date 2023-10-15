; This is the stuff that deals with sending dialed digits down the serial link.
; For `GetDialPulseCount`, see `./dial.asm`
; For `WriteSerial`, see `./serial.asm`


.macro ConvertPulseCountToAscii
    ; This is only used in `GetAndSendDigit`
    ; It's responsible for converting the pulse count in `_digit`
    ; to an ASCII representation.

    ; If digit is zer0, ignore it
    tst _digit
    breq endAsciiCount
    
    ; If there is 10 pulses, the digit "0" has been dialed
    cpi _digit, 10

    ; If it's not 10 pulses, skip to the end - the number of pulses is the
    ; digit dialled
    brne digitFound
    
    ; But it it was 10 pulses, clear _digit so that it;s the digit "0"
    clr _digit

digitFound:
    ; Convert the digit to an ASCII character by adding the value of ASCII "0"
    ; We could achieve the same effect by using a logical OR...
    add _digit, _asciiZero
endAsciiCount:
.endMacro


.macro GetAndSendADigit
    ; Reads the dial. If there are any pulses, convert the number to ASCII and
    ; write it to the serial link

    GetDialPulseCount

    ; If there are no pulses, then we can just skip to the end
    tst _digit
    breq nothingToSend
    
    ; But if there is a digit, convert it to ASCII
    ; and send it to the serial port
    ConvertPulseCountToAscii
    WriteSerial
nothingToSend:
.endMacro
