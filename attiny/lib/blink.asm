    .equ pinBlink = 3

.macro SetupBlink ; not needed if we use SPI
    sbi outputDDR, pinBlink
    sbi outputPort, pinBlink
.endMacro

.macro Blink
    in _io, outputPort
    ldi _quick, 1 << pinBlink
    eor _io, _quick
    out outputPort, _io
.endMacro
