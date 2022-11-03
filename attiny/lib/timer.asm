; This is the delay for the ringer AND the blink test!
; using the blik test as part of the test for the ringer that way.

.macro SetupTimer
    out TCCR1A, _zero           ; Normal mode

    ldi _io, timer1Prescalar
    out TCCR1B, _io

    ldi _io, high(ringerTicks)
    out OCR1AH, _io

    ldi _io, low(ringerTicks)
    out OCR1AL, _io
.endmacro

.macro WaitForRingerTicks
    out TCNT1H, _zero
    out TCNT1L, _zero           ; zero the timer to wait
    ldi _io, (1 << OCF1A)       ; and clear the output compare flag
    out TIFR, _io
waitForTimer1:
    in _check, TIFR
    sbrs _check, OCF1A
    rjmp waitForTimer1
.endmacro

.macro TestDelay ; parameter = number of loops - 40 for one second
    ldi _quick, @0
delay:
    WaitForRingerTicks
    dec _quick
    brne delay
.endmacro
