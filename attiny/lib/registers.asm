    ; r1 - reserved for test utilities
    ; r2 - reserved for test utilities
    ; r3 - reserved for test utilities
    .def ioReg, r4
    .def dummyZeroReg = r15
    .def quickReg = r16
    ; r26 r27 - X
    ; r28 r29 - Y
    ; r30 r31 - Z

.macro setupStackAndReg
    cli
    ldi quickReg, high(RAMEND)
    out SPH, quickReg
    ldi quickReg, low(RAMEND)
    out SPL, quickReg
    clr dummyZeroReg
.endMacro
