    ; This is stuff for reading the dial which uses The 8-bit Timer/Counter 0
    ; to count pulses coming from the dial.
    ; For `skip_instruction_dial_inactive`, see `gpio.asm`


    ; Timer/Counter 0 can either count the rising edge or the falling edge of
    ; pulses coming in to the T0/PD4 pin
    .equ T0_falling = (1 << CS02) | (1 << CS01)
    .equ T0_rising = (1 << CS02) | (1 << CS01) | (1 << CS00)


.macro setup_dial
    ; Set the counter to normal mode
    out TCCR0A, _zero

    ; Set the counter to count rising edges on the T0 pin
    ; PD4 stops acting as a GPIO at this point
    ldi _io, T0_rising
    out TCCR0B, _io

    reset_or_abort_dialing
    ldi _ascii_zero, '0'
.endMacro


.macro get_dial_pulse_count
    ; Clear the digit value so that when we "fall through" this macro
    ; with no digit dialled yet, the result is correctly zero
    clr _dialled_digit

    ; If the dial is active then the full pulse count is not yet available
    skip_instruction_dial_inactive
    rjmp got_pulse_count

    ; Otherwise get the pulse count from the TimerCounter0 register quickly
    ; before the user starts to dial` another digit
    in _dialled_digit, TCNT0

    ; And clear the TimerCounter0 register, ready for the next digit
    out TCNT0, _zero
got_pulse_count:
.endMacro


.macro reset_or_abort_dialing
    ; Called either from macros in this file or from `receiver_picked_up` or `receiver_put_down` in
    ; `./hook-state.asm`.
    ; Clear the counter ready for pulses to come in
    ; And "throw away" any pulses that may have accumulated in TimerCounter0
    ; "by accident"

    clr _dialled_digit
    out TCNT0, _dialled_digit
.endMacro
