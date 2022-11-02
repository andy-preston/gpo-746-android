    .equ pinBlink = 3

.macro setupBlink ; not needed if we use SPI
    sbi outDDR, pinBlink
    sbi outPort, pinBlink
.endMacro

.macro blink
    in ioReg, outPort
    ldi quickReg, 1 << pinBlink
    eor ioReg, quickReg
    out outPort, ioReg
.endMacro

