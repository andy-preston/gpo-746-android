    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"

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
