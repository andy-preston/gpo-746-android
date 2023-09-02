
    .equ t0Falling = (1 << CS02) | (1 << CS01)
    .equ t0Rising = (1 << CS02) | (1 << CS01) | (1 << CS00)

.macro SetupTimerCounter0
    out TCCR0A, _zero          ; Normal mode
    ldi _io, t0Rising          ; !!!! D4 isn't a noraml input any more !!!!
    out TCCR0B, _io            ; Set source / prescale
    out TCNT0, _zero           ; Clear the counter
.endMacro

.macro SetupDial
    SetupTimerCounter0
    ldi _asciiZero, '0'
    clr _digit
.endMacro

.macro GetDialPulseCount
    clr _digit
    SkipDialInactive
    rjmp gotPulseCount
    in _digit, TCNT0                     ; get pulses from the counter
    out TCNT0, _zero                     ; Clear counter, ready for next time
gotPulseCount:
.endMacro

.macro GetAsciiPulseCount
    GetDialPulseCount
    tst _digit                           ; If digit is zero
    breq endAsciiCount                   ; ...there's nothing worth having
    cpi _digit, 10                       ; if it's not 10 pulses ("0" digit)
    brne digitFound                      ; ... it's just an ordinary digit
    clr _digit                           ; ... otherewise use a zero
digitFound:
    add _digit, _asciiZero               ; convert integer to ASCII char
endAsciiCount:
.endMacro

.macro AbortDialing
    clr _digit                           ; Throw away any pulses counted
    out TCNT0, _digit                    ; ... and any "hidden" in the counter
.endMacro
