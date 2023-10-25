    .include "prelude.asm"
    .include "timer.asm"
    .include "serial.asm"
    .include "gpio.asm"
    .include "blinks.asm"

    Setup20msTimer
    SetupSerial

theTop:
    LoadZ digitsToSend
    BlinkFlip
sendDigit:
    WaitForMultiple20ms 0x01
    lpm _digit, Z+
    cpi _digit, ' '
    breq theTop
    WriteSerial
    rjmp sendDigit

digitsToSend:
    .db "Testing1234 "