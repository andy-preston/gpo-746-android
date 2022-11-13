
    .equ t0Falling = (1 << CS02) | (1 << CS01)
    .equ t0Rising = (1 << CS02) | (1 << CS01) | (1 << CS00)

.macro SetupTimerCounter0
    out TCCR0A, _zero         ; Normal mode
    ldi _io, t0Rising         ; !!!! D4 isn't a noraml input any more !!!!
    out TCCR0B, _io           ; Set source / prescalar
    out TCNT0, _zero          ; Clear the counter
.endMacro

.macro SetupDial
    SetupTimerCounter0
    ldi _asciiZero, '0'
    clr _count
.endMacro

.macro GetDialPulseCount
    in _count, TCNT0          ; get pulses from the counter
    out TCNT0, _zero          ; Clear counter, ready for next time
.endMacro

.macro GetAsciiPulseCount
    GetDialPulseCount
    cpi _count, 10            ; if it's not 10 pulses ("0" digit)
    brne digitFound           ; ... it's just an ordinary digit
    clr _count                ; ... otherewise use a zero
digitFound:
    add _count, _asciiZero    ; convert integer to ASCII char
.endMacro
