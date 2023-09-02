    .include "prelude.asm"
    .include "timer.asm"
    .include "serial.asm"
    .include "gpio.asm"
    .include "blinks.asm"

    SetupTimer
    SetupSerial

theTop:
    LoadZ digitsToSend
    BlinkFlip
sendDigit:
    TestDelay 0x01
    lpm _digit, Z+
    cpi _digit, ' '
    breq theTop
    WriteSerial
    rjmp sendDigit

digitsToSend:
    .db "Testing1234 "