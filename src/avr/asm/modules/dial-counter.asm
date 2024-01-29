    ; This is stuff for reading the dial which uses The 8-bit Timer/Counter 0
    ; to count pulses coming from the dial.
    ; For `skip_dial_inactive`, see `gpio.asm`


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

    ; The counter is probably already zero - but let's zero it anyway
    ; just in case.
    out TCNT0, _zero

    ; We skip sending a digit if it's zero, so this is a sensible default
    clr _dialled_digit
.endMacro


.macro get_dial_pulse_count
    ; If the dial is active then the full pulse count is not yet available
    ; and there's nothing to do.
    skip_dial_inactive
    rjmp end_of_pulse_count

    ; There may be a digit available now, or perhaps nothing's happened and
    ; it's zero.
    in _dialled_digit, TCNT0

    ; If it's zero, again, we've got nothing to do.
    tst _dialled_digit
    breq end_of_pulse_count

    ; If it's not zero, clear the counter ready for the next one.
    out TCNT0, _zero

end_of_pulse_count:
.endMacro
