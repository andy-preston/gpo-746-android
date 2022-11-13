    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "lib/registers.asm"
    .include "lib/gpio.asm"
    .include "lib/prescale.asm"
    .include "lib/timer.asm"
    .include "lib/dial.asm"

progStart:
    SetupStackAndReg
    SetupOutputs
    SetupTimer
    SetupDial
    BlinkOff

checkDial:
    SkipDialActive               ; If the dial is inactive
    rjmp checkDial               ; flash out the count we may have accumulated

    GetDialPulseCount
    BlinkCount
    rjmp checkDial

