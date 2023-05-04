    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/constants.asm"
    .include "attiny/modules/timer.asm"

    SetupOutputs
    SetupTimer

theTop:
    BlinkOn
    SendOffHookSignal
    TestDelay 0x20

    BlinkOff
    SendOnHookSignal
    TestDelay 0x20

    rjmp theTop
