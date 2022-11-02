; TODO: do a proper delay that uses Timer2

.macro delayLoopR
    mov r1, @0
delay:
    ldi quickReg, 0xFF
    mov r2, quickReg
outerDelay:
    ldi quickReg, 0xFF
    mov r3, quickReg
innerDelay:
    dec r3
    brne innerDelay
    dec r2
    brne outerDelay
    dec r1
    brne delay
.endMacro

.macro delayLoopI
    ldi quickReg, @0
    delayLoopR quickReg
