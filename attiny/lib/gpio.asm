    .equ outputPort = PORTB
    .equ outputDDR = DDRB

    .equ pinBlink = 3
    .equ pinRTS = 0

    .equ inputPort = PORTD
    .equ inputPins = PIND
    .equ inputDDR = DDRD

    .equ pinRI = 0

.macro SetupBlink
    sbi outputDDR, pinBlink
    sbi outputPort, pinBlink
.endMacro

.macro Blink
    sbic outputPort, pinBlink
    rjmp blinkSet
    sbi outputPort, pinBlink
    rjmp blinkEnd
blinkSet:
    cbi outputPort, pinBlink
blinkEnd:
.endMacro

.macro SetupRTS
    sbi outputDDR, pinRTS
.endmacro

.macro SetRTS
    sbi outputPort, pinRTS
.endmacro

.macro ResetRTS
    cbi outputPort, pinRTS
.endmacro

.macro SkipOnRI
    sbis inputPins, pinRI
.endmacro

.macro SkipOnNoRI
    sbic inputPins, pinRI
.endmacro
