    .def _zero = r1
    .def _check = r2
    .def _loops = r16
    .def _io =  r17
    .def _bell = r18
    .def _digit = r19
    .def _asciiZero = r20
    ; r26 r27 - X
    ; r28 r29 - Y
    ; r30 r31 - Z

.macro SetupStackAndReg
    cli
    ; There's only SPL because ATTiny2313 doesn't have enough RAM to need SPH
    ldi _io, RAMEND
    out SPL, _io
    clr _zero
.endMacro

.macro LoadZ
    ldi ZL, low(@0 << 1)
    ldi ZH, high(@0 << 1)
.endmacro
