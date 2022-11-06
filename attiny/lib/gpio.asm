    .equ outputPort = PORTB
    .equ outputDDR = DDRB

    .equ inputPort = PORTD
    .equ inputPins = PIND
    .equ inputDDR = DDRD

    .equ pinBlink = 3

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
