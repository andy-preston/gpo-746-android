; To keep things as simple to implement as possible, all output are done on
; one GPIO port and all inputs on the other.

    .equ output_port = PORTB
    .equ output_DDR = DDRB

    ; Switching the RI handshaking line on the CH340G to signal to the other
    ; side that the hook is up.
    .equ pin_out_pick_up_RI = 0

    ; Two pins to switch the bells. This should be an AC sine wave. But the
    ; square wave you get out of GPIO seems to sound alright.
    .equ pin_out_ding = 1
    .equ pin_out_dong = 2

    ; "Internal" output to switch the amplifier on or off.
    .equ pin_out_amplifier = 3

    ; Diagnostic LED free for blinking for whatever may like to blink it.
    .equ pin_out_LED = 4

    ; These are the last output pins to use, just in case,
    ; in some point in the future, we might want an in-circuit programmer.
    .equ pin_out_MOSI_B5 = 5
    .equ pin_out_MISO_B6 = 6
    .equ pin_out_UCSK_B7 = 7

    ; The output pins for the DDR.
    .equ control_outputs = (1 << pin_out_LED) | (1 << pin_out_pick_up_RI)
    .equ dial_outputs = (1 << pin_out_ding) | (1 << pin_out_dong)
    .equ outputs = control_outputs | dial_outputs


    .equ input_port = PORTD
    .equ input_pins = PIND
    .equ input_DDR = DDRD

    ; The two pins are shared with the serial port. We're not using RX so,
    ; "in an emergency", we can use that as an input.
    .equ pin_in_RX_D0 = 0
    .equ pin_in_TX_D1 = 1

    ; "Internal" input for the receiver hook.
    .equ pin_in_receiver = 2

    ; The dial's grey line switches to blue (VCC) when the dial is active.
    .equ pin_in_dial_active_grey = 3

    ; D4 isn't used as a GPIO but for as the T0 pin for TimerCounter0 to count
    ; pulses from the dial.
    ; See `setup_dial` in `./dial-counter.asm` for usage details.
    ; The pink line is normally closed and held to orange (GND).
    ; It goes open (into a pulled-up state) for 50ms for each pulse.
    .equ pin_in_dial_pulse_pink = 4

    ; Used to read the RTS line on the CH340G to see if we should ring or not.
    .equ pin_in_incoming_RTS = 5

    ; Used to read the DTR line on the CH340G to see if the amplifier should be
    ; on or not.
    .equ pin_in_amp_required_DTR = 6

    ; D7 is going spare at the moment.
    .equ pin_in_absent_D7 = 7


.macro setup_outputs
    ldi _io, outputs
    out output_DDR, _io

.ifDevice ATmega644P
    out DDRA, _all_bits_high
    out PORTA, _zero
    out DDRC, _all_bits_high
    out PORTC, _zero
.endIf

    ; Clear all outputs except the LED.
    ; To ensure everything is switched off...
    ; ... apart from what's switched on, of course.
    ldi _io, 1 << pin_out_LED
    out output_port, _io
.endMacro
