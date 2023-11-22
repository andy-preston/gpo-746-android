; The 16-bit TimerCounter1 is used for a delay for the ringer AND the blink
; test! Using the blink test as part of the test for the ringer that way.

; The 8-bit TimerCounter0 is used as part of the dial reading procedures
; but it's setup and operation code isn't here, see `dial-counter.asm`

; For timer1_clock_select and timer1_20ms_ticks calculations, see:
;     src/buildSrc/src/main/kotlin/gpo746/AvrConstants.kt

; For details of @timer1_clock_select@ and @timer1_20ms_ticks@,
; see `src/buildSrc/src/main/kotlin/gpo746/AvrConstants.kt`

.macro setup_20ms_timer
    ; Set the timer in normal mode rather than an of the
    ; PWM options, etc.
    out TCCR1A, _zero

    ; Set up the timer pre-scaler bits
    ldi _io, @timer1_clock_select@
    out TCCR1B, _io

    ; Set the number of timer ticks to count to
    ; in the timer's output compare register
    ldi _io, high(@timer1_20ms_ticks@)
    out OCR1AH, _io
    ldi _io, low(@timer1_20ms_ticks@)
    out OCR1AL, _io
.endMacro


.macro start_20ms_wait
    ; start a timer count at zero
    out TCNT1H, _zero
    out TCNT1L, _zero
    ; clear the output compare flag
    ; which will be set again when the timer count is complete
    ldi _io, (1 << OCF1A)
    out TIFR, _io ; TIFR (56) is out of range for `sbi`
.endMacro


.macro complete_20ms_wait
    ; Wait for @timer1_20ms_ticks@ ticks to complete
    ; at which point the timer sets the output compare flag again
wait_for_timer1:
    in _timer_wait, TIFR
    sbrs _timer_wait, OCF1A
    rjmp wait_for_timer1
.endMacro


.macro wait_for_20ms
    start_20ms_wait
    complete_20ms_wait
.endMacro


.macro wait_for_multiple_20ms
    ; Used in tests where @timer1_20ms_ticks@ is too short for a human to notice.
    ; Because we're re-using a delay loop with a specific purpose just to
    ; get a human visible delay, the timing is a bit odd here:
    ; The parameter is duration with a value of 50 indicating 1 second
    ; (Plus a little bit extra as we're ignoring the time it takes the loop
    ; itself to execute)
    ldi _delay_repeat, @0
delay:
    wait_for_20ms
    dec _delay_repeat
    brne delay
.endMacro

.macro wait_for_a_second
    ; I've found myself wanting a one-second delay in a few tests and
    ; found myself typing wait_for_multiple_20ms 0x50 . which isn't what we
    ; want... the hexadecimal obsession of the assembly programmer strikes!
    wait_for_multiple_20ms 50
.endMacro