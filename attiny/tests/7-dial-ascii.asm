    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/prescale.asm"
    .include "attiny/modules/dial.asm"
    .include "attiny/modules/serial.asm"
    .include "attiny/modules/dial-serial.asm"

    SetupOutputs
    SetupDial
    SetupSerial

checkDial:
    GetAndSendADigit
    rjmp checkDial
