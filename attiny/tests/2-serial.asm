    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "lib/registers.asm"
    .include "lib/gpio.asm"
    .include "lib/prescale.asm"
    .include "lib/timer.asm"
    .include "lib/serial.asm"

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; This is expecting to see something like ../../ch340g-test/prototype.c
; At the other end of it's USB Lead, see ../../ch340g-test/README.md
; for details.
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

progStart:
    SetupStackAndReg
    SetupOutputs
    SetupTimer
    SetupSerial

checkRI:
    SkipOnRTS
    rjmp noRTS
    LoadZ rtsYes
    rjmp statusOut

noRTS:
    LoadZ rtsNo

statusOut:
    lpm _io, Z+
    WriteSerial
    cpi _io, ' '
    brne statusOut

checkSerial:
    ReadSerial
    cpi _digit, 0
    brne checkOne

    Blink
    TestDelay 0x10
    rjmp checkSerial

checkOne:
    cpi _digit, '1'
    brne notOne
    LoadZ one
    rjmp numberOut

notOne:
    cpi _digit, '2'
    brne notTwo
    LoadZ two
    rjmp numberOut

notTwo:
    cpi _digit, '3'
    brne notThree

    sbic outputPort, pinRTS  ; Toggle RTS everytime we get to "3"
    rjmp setRTS
    sbi outputPort, pinRTS
    rjmp endRTS
setRTS:
    cbi outputPort, pinRTS
endRTS:

    LoadZ three
    rjmp numberOut

notThree:
    cpi _digit, '4'
    brne notFour
    LoadZ four
    rjmp numberOut

notFour:
    LoadZ unknown

numberOut:
    lpm _io, Z+
    WriteSerial
    cpi _io, '\j'
    brne numberOut
    rjmp checkRI

rtsYes:
    .db "RTS "
rtsNo:
    .db "--- "
one:
    .db "   one\m\j"
two:
    .db "   two\m\j"
three:
    .db " three\m\j"
four:
    .db "  four\m\j"
unknown:
    .db "  ????\m\j"
