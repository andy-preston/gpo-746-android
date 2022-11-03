    .def _zero = r1
    .def _check = r2
    .def _quick = r16
    .def _count = r17
    .def _io =  r18
    .def _digit = r19
    ; r26 r27 - X
    ; r28 r29 - Y
    ; r30 r31 - Z

.macro SetupStackAndReg
    cli
    ; There's only SPL here ecause ATTiny2313 doesn't have enough RAM
    ; To need SPH
    ldi _quick, RAMEND
    out SPL, _quick
    clr _zero
.endMacro
