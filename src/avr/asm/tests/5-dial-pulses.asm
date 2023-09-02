    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "dial.asm"
    .include "blinks.asm"

    SetupOutputs
    SetupTimer
    SetupDial
    BlinkOff
checkDial:
    GetDialPulseCount
    tst _digit
    breq checkDial

    BlinkCount
    rjmp checkDial
