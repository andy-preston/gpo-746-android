    ; This is stuff for reading the dial which uses The 8-bit TimerCounter0
    ; to count pulses coming from the dial.
    ; For `SkipDialInactive`, see `gpio.asm`


    ; TimerCounter0 can either count the rising edge or the falling edge of
    ; pulses coming in to the T0/PD4 pin
    .equ t0Falling = (1 << CS02) | (1 << CS01)
    .equ t0Rising = (1 << CS02) | (1 << CS01) | (1 << CS00)


.macro SetupDial
    ; Set the counter to normal mode
    out TCCR0A, _zero

    ; Set the counter to count rising edges on the T0 pin    
    ; PD4 stops acting as a GPIO at this point
    ldi _io, t0Rising
    out TCCR0B, _io

    ResetOrAbortDialing
    ldi _asciiZero, '0'
.endMacro


.macro GetDialPulseCount
    ; Clear the digit value so that when we "fall through" this macro
    ; with no digit dialed yet, the result is correctly zero
    clr _digit

    ; If the dial is active then the full pulse count is not yet available
    SkipDialInactive
    rjmp gotPulseCount

    ; Otherwise get the pulse count from the TimerCounter0 register quickly
    ; before the user starts to dial` another digit
    in _digit, TCNT0

    ; And clear the TimerCounter0 register, ready for the next digit
    out TCNT0, _zero
gotPulseCount:
.endMacro


.macro ResetOrAbortDialing
    ; Called either from macros in this file or from `PickedUp` or `PutDown` in
    ; `./hook.asm`.
    ; Clear the counter ready for pulses to come in
    ; And "throw away" any pulses that may have accumulated in TimerCounter0
    ; "by accident"

    clr _digit
    out TCNT0, _digit
.endMacro
