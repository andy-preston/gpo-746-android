    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/constants.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/serial.asm"

    SetupTimer
    SetupSerial

theTop:
    LoadZ digitsToSend
sendDigit:
    lpm _digit, Z+
    cpi _digit, ' '
    breq theTop
    WriteSerial
    rjmp sendDigit

digitsToSend:
    .db "079332376 "