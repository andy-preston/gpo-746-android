; To keep things as simple to implement as possible, all output are done on
; one GPIO port and all inputs on the other.

    .equ output_port = PORTB
    .equ output_DDR = DDRB

    .equ pin_out_RI = 0
    .equ pin_out_ding = 1
    .equ pin_out_dong = 2
    .equ pin_out_amplifier = 3
    .equ pin_out_LED = 4
    .equ pin_out_MOSI_B5 = 5
    .equ pin_out_MISO_B6 = 6
    .equ pin_out_UCSK_B7 = 7

    .equ input_port = PORTD
    .equ input_pins = PIND
    .equ input_DDR = DDRD

    .equ pin_in_RX_D0 = 0
    .equ pin_in_TX_D1 = 1
    .equ pin_in_spare_D2 = 2
    .equ pin_in_receiver = 3
    ; D4 isn't used as a GPIO but for as the T0 pin for TimerCounter0 to count
    ; pulses from the dial.
    ; See `setup_20ms_timerCounter0` in `./dial-counter.asm` for usage details.
    .equ pin_in_dial_pink = 4  ; Pulse - orange (GND) -> pink low
    .equ pin_in_RTS = 5
    .equ pin_in_DTR = 6
    .equ pin_in_dial_grey = 5  ; Active - blue (VCC) -> grey high
    .equ pin_in_absent_D7 = 7


.macro setup_outputs
    ldi _io, (1 << pin_out_LED) | (1 << pin_out_RI) | (1 << pin_out_ding) | (1 << pin_out_dong)
    out output_DDR, _io
    sbi output_port, pin_out_LED
.endMacro


.macro blink_on
    sbi output_port, pin_out_LED
.endMacro


.macro blink_off
    cbi output_port, pin_out_LED
.endMacro


.macro switch_amp_on
    sbi output_port, pin_out_amplifier
.endMacro


.macro switch_amp_off
    cbi output_port, pin_out_amplifier
.endMacro


.macro send_picked_up_signal
    sbi output_port, pin_out_RI
.endMacro


.macro send_put_down_signal
    cbi output_port, pin_out_RI
.endMacro


.macro skip_on_no_incoming
    sbic input_pins, pin_in_RTS
.endMacro


.macro skip_on_no_amp_required
    sbic input_pins, pin_in_DTR
.endMacro


.macro skip_when_picked_up
    sbis input_pins, pin_in_receiver
.endMacro


.macro skip_dial_inactive
    sbic input_pins, pin_in_dial_grey
.endMacro
