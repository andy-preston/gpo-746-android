    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "attiny/modules/registers.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/prescale.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/serial.asm"
    .include "attiny/modules/blinks.asm"

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

checkRTS:
    SkipOnNoIncomming          ; TODO: better if this can be SkipOnNoIncomming
    rjmp yesIncomming          ; As that's what main code uses
    LoadZ rtsNo
    rjmp statusOut

yesIncomming:
    LoadZ rtsNo

statusOut:
    lpm _digit, Z+
    WriteSerial
    cpi _digit, ' '
    brne statusOut

checkSerial:
    ReadSerial
    cpi _digit, 0
    brne checkOne

    BlinkFlip
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

    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    ;
    ; TODO: RI is our output not RTS
    ; And this is just crap!
    ;
    ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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
    lpm _digit, Z+
    WriteSerial
    cpi _digit, '\j'
    brne numberOut
    rjmp checkRTS

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
