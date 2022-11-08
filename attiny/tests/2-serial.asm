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
    SetupBlink
    SetupRTS
    SetupTimer
    SetupSerial

checkSerial:
    SkipOnRI
    rjmp noRI
    LoadZ ring
    rjmp serialOut

noRI:
    ReadSerial

    cpi _digit, '1'
    brne notOne
    LoadZ one
    rjmp serialOut

notOne:
    cpi _digit, '2'
    brne notTwo
    LoadZ two
    rjmp serialOut

notTwo:
    cpi _digit, '3'
    brne notThree
    LoadZ three
    rjmp serialOut

notThree:
    cpi _digit, '4'
    brne notFour
    LoadZ four
    rjmp serialOut

notFour:
    cpi _digit, 'R'
    brne notRTS
    SetRTS
    rjmp checkSerial

notRTS:
    Blink
    TestDelay 0x20

serialOut:
    lpm _io, Z+
    WriteSerial
    cpi _io, '\j'
    brne serialOut
    rjmp checkSerial

ring:
    .db "ring\m\j"
one:
    .db "one\m\j\@"
two:
    .db "two\m\j\@"
three:
    .db "three\m\j\@"
four:
    .db "four\m\j"
