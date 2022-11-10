    .equ outputPort = PORTB
    .equ outputDDR = DDRB

    .equ pinBlink = 3
    .equ pinRI = 0

    .equ inputPort = PORTD
    .equ inputPins = PIND
    .equ inputDDR = DDRD

    .equ pinRTS = 6

.macro SetupOutputs
    ldi _io, (1 << pinBlink) | (1 << pinRI)
    out outputDDR, _io
    sbi outputPort, pinBlink
.endMacro

.macro Blink
    sbic outputPort, pinBlink  ; If LED is clear/off
    rjmp blinkOff              ; don't switch it off
    sbi outputPort, pinBlink   ; switch it on instead
    rjmp blinkEnd
blinkOff:
    cbi outputPort, pinBlink   ; If it was set/on, switch it off
blinkEnd:
.endMacro

.macro SetRI
    sbi outputPort, pinRI
.endmacro

.macro ResetRI
    cbi outputPort, pinRI
.endmacro

.macro SkipOnRTS
    sbis inputPins, pinRTS
.endmacro

.macro SkipOnNoRTS
    sbic inputPins, pinRTS
.endmacro
