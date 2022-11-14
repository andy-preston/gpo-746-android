    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "modules/registers.asm"
    .include "modules/gpio.asm"
    .include "modules/prescale.asm"
    .include "modules/timer.asm"
    .include "modules/dial.asm"
    .include "modules/serial.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
    SetupTimer
    SetupDial
    SetupSerial

checkDial:
    GetAsciiPulseCount
    tst _digit             ; Skip counting pulses if there are none
    breq checkDial         ; we don't want to output "nothing"
    mov _io, _digit
    WriteSerial
    rjmp checkDial
