    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "lib/registers.asm"
    .include "lib/ports.asm"
    .include "lib/prescale.asm"
    .include "lib/serial.asm"
    .include "lib/timer.asm"
    .include "lib/blink.asm"

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
    SetupSerial

checkSerial:
    ReadSerial

    cpi _digit, '1'
    brne notOne
    ldi ZL, low(one)
    ldi ZH, high(one)
    rjmp serialOut

notOne:
    cpi _digit, '2'
    brne notTwo
    ldi ZL, low(two)
    ldi ZH, high(two)
    rjmp serialOut

notTwo:
    cpi _digit, '3'
    brne notThree
    ldi ZL, low(three)
    ldi ZH, high(three)
    rjmp serialOut

notThree:
    cpi _digit, '4'
    brne notFour
    ldi ZL, low(four)
    ldi ZH, high(four)
    rjmp serialOut

notFour:
    Blink
    TestDelay 0x20
    rjmp checkSerial

serialOut:
    lpm _io, Z+
    WriteSerialCharacter
    cpi _io, '\n'
    brne serialOut
    rjmp checkSerial

one:
    .db "one\n"
two:
    .db "two\n"
three:
    .db "three\n"
four:
    .db "four\n\n"
