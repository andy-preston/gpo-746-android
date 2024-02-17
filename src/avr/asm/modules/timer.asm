; The 16-bit TimerCounter1 is used for a delay for the ringer AND the blink
; test! Using the blink test as part of the test for the ringer that way.

; The 8-bit TimerCounter0 is used as part of the dial reading procedures
; so it's setup and operation code isn't here, see `dial-counter.asm`

; For timer1_clock_select and timer1_20ms_ticks calculations, see:
;     src/buildSrc/src/main/kotlin/gpo746/AvrConstants.kt

; For details of @timer1_clock_select@ and @timer1_20ms_ticks@,
; see `src/buildSrc/src/main/kotlin/gpo746/AvrConstants.kt`

.macro setup_timer
    ; Set the timer in normal mode rather than any of the PWM options, etc.
    out TCCR1A, _zero
    ; Set up the timer pre-scaler bits
    ldi _io, @timer1_clock_select@
    out TCCR1B, _io
.endMacro


.macro set_timer_interval_to_20ms
    ; Set the number of timer ticks to count up to
    ; in the timer's output compare register
    ldi _io, high(@timer1_20ms_ticks@)
    out OCR1AH, _io
    ldi _io, low(@timer1_20ms_ticks@)
    out OCR1AL, _io
.endMacro


.macro set_timer_interval_to_30ms
    ; Set the number of timer ticks to count up to
    ; in the timer's output compare register
    ldi _io, high(@timer1_30ms_ticks@)
    out OCR1AH, _io
    ldi _io, low(@timer1_30ms_ticks@)
    out OCR1AL, _io
.endMacro


.macro start_interval_timer
    ; start a timer count at zero
    out TCNT1H, _zero
    out TCNT1L, _zero
    ; clear the output compare flag
    ; which will be set again when the timer count is complete
    ldi _io, (1 << OCF1A)
    out TIFR, _io ; TIFR (56) is out of range for `sbi`
.endMacro


.macro skip_if_interval_complete
    ; skip the next instruction if @timer1_20ms_ticks@ have passed
    ; at which point the timer sets the output compare flag again
    in _timer_wait, TIFR
    sbrs _timer_wait, OCF1A
.endMacro


.macro wait_for_interval
    ; Wait for @timer1_20ms_ticks@ ticks to complete
wait_for_timer1:
    skip_if_interval_complete
    rjmp wait_for_timer1
.endMacro


; The following `wait_for_XXX` macros naming specific time intervals are only
; for use in testing where @timer1_20ms_ticks@ is too short for a human to
; notice. The timing values aren't particularly accurate because we're ignoring
; the few cycles it takes to set up the timer before it runs. But it's close
; enough for testing. Also some of the code winds up being a tad inefficient.
; But, again, that's no big deal during testing. I wouldn't like that sort of
; thing in "production" though.


.macro wait_for_20ms
    set_timer_interval_to_20ms
    start_interval_timer
    wait_for_interval
.endMacro


.macro wait_for_multiple_20ms
    ; Used in tests . Because we're re-using a delay loop with a specific purpose just
    ; to get a human visible delay, the timing is a bit odd here:
    ; The parameter is duration with a value of 50 indicating 1 second
    ; (Plus a little bit extra as we're ignoring the time it takes the loop
    ; itself to execute)
    ldi _delay_repeat, @0
delay:
    wait_for_20ms
    dec _delay_repeat
    brne delay
.endMacro


.macro wait_for_fifth_of_a_second
    wait_for_multiple_20ms 10
.endMacro


.macro wait_for_half_a_second
    wait_for_multiple_20ms 25
.endMacro


.macro wait_for_a_second
    wait_for_multiple_20ms 50
.endMacro


.macro wait_for_two_seconds
    wait_for_multiple_20ms 100
.endMacro


.macro wait_for_three_seconds
    wait_for_multiple_20ms 150
.endMacro


.macro wait_for_four_seconds
    wait_for_multiple_20ms 200
.endMacro


.macro wait_for_five_seconds
    wait_for_multiple_20ms 250
.endMacro
