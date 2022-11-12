    .equ outputPort = PORTB
    .equ outputDDR = DDRB

    .equ pinRI = 0
    .equ pinBlink = 3

    .equ inputPort = PORTD
    .equ inputPins = PIND
    .equ inputDDR = DDRD

    .equ pinHook = 3
    .equ pinDialPink = 4      ; Dial pulse - organge (GND) -> pink low
    .equ pinDialGrey = 5      ; Dial active - blue (VCC) -> grey high
    .equ pinRTS = 6

.macro SetupOutputs
    ldi _io, (1 << pinBlink) | (1 << pinRI)
    out outputDDR, _io
    sbi outputPort, pinBlink
.endMacro

.macro BlinkOn
    sbi outputPort, pinBlink
.endMacro

.macro BlinkOff
    cbi outputPort, pinBlink
.endMacro

.macro Blink
    sbic outputPort, pinBlink  ; If LED is clear/off
    rjmp blinkOff              ; don't switch it off
    BlinkOn                    ; switch it on instead
    rjmp blinkEnd
blinkOff:
    BlinkOff                   ; If it was set/on, switch it off
blinkEnd:
.endMacro

.macro SetRI
    sbi outputPort, pinRI
.endMacro

.macro ResetRI
    cbi outputPort, pinRI
.endMacro

.macro SkipOnRTS
    sbis inputPins, pinRTS
.endMacro

.macro SkipOnNoRTS
    sbic inputPins, pinRTS
.endMacro

.macro SkipOnHook
    sbis inputPins, pinHook
.endMacro

.macro SkipOffHook
    sbic inputPins, pinHook
.endMacro

.macro SkipDialInactive
    sbic inputPins, pinDialGrey
.endMacro
