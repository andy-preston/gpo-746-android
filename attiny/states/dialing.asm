.macro DialState
    GetAsciiPulseCount
    tst _digit             ; Skip counting pulses if there are none
    breq nothingToSend     ; we don't want to output "nothing"
    WriteSerial
nothingToSend:
.endMacro
