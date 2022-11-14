    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "lib/registers.asm"
    .include "lib/gpio.asm"
    .include "lib/prescale.asm"
    .include "lib/timer.asm"
    .include "lib/dial.asm"
    .include "lib/serial.asm"

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
