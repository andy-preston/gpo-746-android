; To keep things as simple to implement as possible, all output are done on
; one GPIO port and all inputs on the other.

    .equ outputPort = PORTB
    .equ outputDDR = DDRB

    .equ pinRI = 0
    .equ pinDing = 1
    .equ pinDong = 2
    .equ pinBlink = 3

    .equ inputPort = PORTD
    .equ inputPins = PIND
    .equ inputDDR = DDRD

    ; PD4 is included here only for completeness.
    ; It's not used as a GPIO but for as the T0 pin for TimerCounter0 to count
    ; pulses from the dial.
    ; See `Setup20msTimerCounter0` in `./dial.asm` for usage details.

    .equ pinHook = 3
    .equ pinDialPink = 4                 ; Pulse - orange (GND) -> pink low
    .equ pinDialGrey = 5                 ; Active - blue (VCC) -> grey high
    .equ pinRTS = 6

.macro SetupOutputs
    ldi _io, (1 << pinBlink) | (1 << pinRI) | (1 << pinDing) | (1 << pinDong)
    out outputDDR, _io
    sbi outputPort, pinBlink
.endMacro

.macro BlinkOn
    sbi outputPort, pinBlink
.endMacro

.macro BlinkOff
    cbi outputPort, pinBlink
.endMacro

.macro SendOffHookSignal
    sbi outputPort, pinRI
.endMacro

.macro SendOnHookSignal
    cbi outputPort, pinRI
.endMacro

.macro SkipOnNoIncoming
    sbic inputPins, pinRTS
.endMacro

.macro SkipOffHook
    sbis inputPins, pinHook
.endMacro

.macro SkipDialInactive
    sbic inputPins, pinDialGrey
.endMacro
