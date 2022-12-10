    .equ outputPort = PORTB
    .equ outputDDR = DDRB

    .equ pinRI = 0
    .equ pinDing = 1
    .equ pinDong = 2
    .equ pinBlink = 3

    .equ inputPort = PORTD
    .equ inputPins = PIND
    .equ inputDDR = DDRD

    ; D4 is included here only for completeness.
    ; It's not used as a GPIO. See ./dial.asm for usage details.

    .equ pinHook = 3
    .equ pinDialPink = 4      ; Dial pulse - organge (GND) -> pink low
    .equ pinDialGrey = 5      ; Dial active - blue (VCC) -> grey high
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

.macro SkipOnIncomming
    sbis inputPins, pinRTS
.endMacro

.macro SkipOnNoIncomming
    sbic inputPins, pinRTS
.endMacro

.macro SkipOffHook
    sbis inputPins, pinHook
.endMacro

.macro SkipDialInactive
    sbic inputPins, pinDialGrey
.endMacro
