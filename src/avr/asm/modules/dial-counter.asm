; Debouncing and counting the pulses that come from the dial.
;
; After measuring with a logic analyzer, the pulse width is uniformly,
; approximately 50ms
;
; The debounce algorithm below is driven by a timer and accumulates the 8
; previous states of the input pin. Once 8 ticks have all "seen" high, it's
; considered a single pulse. The count is then cleared but the actual input
; pulse may still be high (referred to as the "dregs"). As long as these
; "dregs" last for less than the time required to accumulate another 8
; consecutive high readings, they won't skew the results.
;
; | Clock | 8 ticks | "Dregs" | Failure case or Ratio              |
; | ----- | ------- | ------- | ---------------------------------- |
; | 6.5ms | 52ms    | -2ms    | Longer pulse than available        |
; | 6.0ms | 48ms    |  2ms    | 24.0                               |
; | 5.5ms | 44ms    |  6ms    |  7.3                               |
; | 5.0ms | 40ms    | 10ms    |  4.0 *                             |
; | 4.5ms | 36ms *  | 14ms    |  2.3                               |
; | 4.0ms | 32ms    | 18ms    |  1.7                               |
; | 3.5ms | 28ms    | 22ms    |  1.3                               |
; | 3.0ms | 24ms    | 26ms    | "Dregs" longer than expected pulse |
;
; start timer
; loop
;     if not active
;         move _dialled_digit <- _pulse_count
;         clear _pulse_count
;         clear _bounce_state
;     else if timer has ticked
;         start timer
;         left shift _bounce_state
;         read input into _bounce_state[0]
;         if _bounce_state == 11111111
;             inc _pulse_count
;             clear _bounce_state
;         end
;     end
;     if _dialled_digit > 0
;         send it in ASCII
;         clear _dialled_digit
;     end
; end

.macro skip_if_5ms_interval_complete
    ; skip the next instruction if timer1_debounce_ticks have passed
    ; at which point the timer sets the output compare flag again
.endMacro


.macro setup_dial
    start_interval_timers
    clr _dialled_digit
    clr _pulse_count
    clr _bounce_state
.endMacro


.macro count_incoming_pulses
    in _timer_wait, TIFR
    sbrc _timer_wait, debounce_interval
    rjmp still_waiting

timer_tick:
    start_interval_timers
    in _io, input_pins
    lsl _bounce_state
    bst _io, pin_in_dial_pulse_pink
    bld _bounce_state, 0
    cp _bounce_state, _all_bits_high
    brne still_waiting

definitely_a_pulse:
    inc _pulse_count
    clr _bounce_state

still_waiting:
.endMacro


.macro get_dial_pulse_count
    ; If the dial is active then the full pulse count is not yet available
    ; and there's nothing to do.
    sbis input_pins, pin_in_dial_active_grey
    rjmp still_counting

finished_counting:
    ; There may be a digit available now, or perhaps nothing's happened and
    ; it's zero.
    mov _dialled_digit, _pulse_count

    ; zero everything out, ready for the next digit
    clr _pulse_count
    clr _bounce_state
    rjmp end_of_pulse_count

still_counting:
    count_incoming_pulses

end_of_pulse_count:
.endMacro
