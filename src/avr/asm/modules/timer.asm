; The 16-bit TimerCounter1 is used for a delay for the ringer, the blink
; test and the pulse debounce.
;
; For timer1_clock_select and timer1 ticks calculations, see:
;     src/buildSrc/src/main/kotlin/gpo746/AvrConstants.kt
; The values from these calculations are written to the compile-time file:
;     "constants.asm"
;

; Easier names for the bits to check in TIFR

    .equ ring_interval = OCF1A
    .equ debounce_interval = OCF1B

.macro setup_timer
    ; Set the timer in normal mode rather than any of the PWM options, etc.
    out TCCR1A, _zero
    ; Set up the timer pre-scaler bits
    ldi _io, timer1_clock_select
    out TCCR1B, _io

    ldi _io, high(timer1_ring_ticks)
    out OCR1AH, _io
    ldi _io, low(timer1_ring_ticks)
    out OCR1AL, _io

    ldi _io, high(timer1_debounce_ticks)
    out OCR1BH, _io
    ldi _io, low(timer1_debounce_ticks)
    out OCR1BL, _io
.endMacro


.macro start_interval_timers
    ; Start the timer counts at zero
    out TCNT1H, _zero
    out TCNT1L, _zero
    ; Clear the output compare flags, which will be set again when the timer
    ; counts are complete. It looks like it's wrong because we're clearing
    ; flags by writing a 1 to them. But that is how it works! Also, we're
    ; using `ldi` and `out` here because TIFR is out of range to be able to
    ; use `sbi`
    ldi _io, (1 << ring_interval) | (1 << debounce_interval)
    out TIFR, _io
.endMacro
