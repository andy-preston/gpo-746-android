    .include "prelude.asm"
    .include "gpio.asm"
    .include "dial.asm"
    .include "serial.asm"
    .include "dial-serial.asm"

    ; Dial multiple digits and they should be sent across the serial device for
    ; detected by the attached device.

    SetupOutputs
    SetupDial
    SetupSerial

checkDial:
    GetAndSendADigit
    rjmp checkDial
