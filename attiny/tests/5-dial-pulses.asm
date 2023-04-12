    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/constants.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/dial.asm"
    .include "attiny/modules/blinks.asm"

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
