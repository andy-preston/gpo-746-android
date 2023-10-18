; The 16-bit TimerCounter1 is used for a delay for the ringer AND the blink
; test! Using the blink test as part of the test for the ringer that way.

; The 8-bit TimerCounter0 is used as part of the dial reading procedures
; but it's setup and operation code isn't here, see `dial.asm`

; For timer1ClockSelect and timer1Ticks calculations, see:
;     src/buildSrc/src/main/kotlin/gpo_746/AvrConstants.kt

; For details of @timer1ClockSelect@ and @timer1Ticks@,
; see `src/buildSrc/src/main/kotlin/gpo_746/AvrConstants.kt`

.macro SetupTimer
    ; Set the timer in normal mode rather than an of the
    ; PWM options, etc.
    out TCCR1A, _zero

    ; Set up the timer pre-scaler bits
    ldi _io, @timer1ClockSelect@
    out TCCR1B, _io

    ; Set the number of timer ticks to count to
    ; in the timer's output compare register
    ldi _io, high(@timer1Ticks@)
    out OCR1AH, _io
    ldi _io, low(@timer1Ticks@)
    out OCR1AL, _io
.endMacro

.macro WaitForRingerTicks
    ; start a timer count at zero
    out TCNT1H, _zero
    out TCNT1L, _zero

    ; clear the output compare flag
    ; which will be set again when the timer count is complete
    ldi _io, (1 << OCF1A)
    out TIFR, _io ; TIFR (56) is out of range for `sbi`

    ; Wait for @timer1Ticks@ ticks to complete
    ; at which point the timer sets the output compare flag again
waitForTimer1:
    in _check, TIFR
    sbrs _check, OCF1A
    rjmp waitForTimer1
.endMacro

.macro TestDelay
    ; Ony used in testing because @time1Ticks@ is too short a duration
    ; for a human being to notice.
    ; Because we're re-using a delay loop with a specific purpose just to
    ; get a human visible delay, the timing is a bit odd here:
    ; The parameter is duration in with a value of 40 indicating 1 second
    ldi _loops, @0
delay:
    WaitForRingerTicks
    dec _loops
    brne delay
.endMacro
