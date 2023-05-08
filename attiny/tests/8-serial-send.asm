    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/constants.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/serial.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/blinks.asm"

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