; This is the delay for the ringer AND the blink test!
; using the blink test as part of the test for the ringer that way.

; For timer1ClockSelect and timer1Ticks calculations, please see:
;     src/buildSrc/src/main/kotlin/gpo_746/AvrConstants.kt

.macro SetupTimer
    out TCCR1A, _zero                    ; Normal mode
    ldi _io, @timer1ClockSelect@
    out TCCR1B, _io
    ldi _io, high(@timer1Ticks@)
    out OCR1AH, _io
    ldi _io, low(@timer1Ticks@)
    out OCR1AL, _io
.endMacro

.macro WaitForRingerTicks
    out TCNT1H, _zero
    out TCNT1L, _zero                    ; zero the timer to wait
    ldi _io, (1 << OCF1A)                ; and clear the output compare flag
    out TIFR, _io                        ; TIFR (56) is out of range for `sbi`
waitForTimer1:
    in _check, TIFR
    sbrs _check, OCF1A
    rjmp waitForTimer1
.endMacro

.macro TestDelay                         ; parameter = number of loops
    ldi _loops, @0                       ; ... 40 for 1 second
delay:
    WaitForRingerTicks
    dec _loops
    brne delay
.endMacro
