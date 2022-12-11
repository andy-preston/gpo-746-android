.macro GetAndSendADigit
    GetAsciiPulseCount
    tst _digit                           ; Skip pulse count if there are none
    breq nothingToSend                   ; ... we don't want to output "nothing"
    WriteSerial
nothingToSend:
.endMacro
