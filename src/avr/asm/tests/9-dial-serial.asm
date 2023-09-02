    .include "prelude.asm"
    .include "gpio.asm"
    .include "dial.asm"
    .include "serial.asm"
    .include "dial-serial.asm"

    SetupOutputs
    SetupDial
    SetupSerial

checkDial:
    GetAndSendADigit
    rjmp checkDial
