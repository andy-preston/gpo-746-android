    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"

    SetupOutputs
    Setup20msTimer

theTop:
    BlinkOn
    SendOffHookSignal
    WaitForMultiple20ms 0x20

    BlinkOff
    SendOnHookSignal
    WaitForMultiple20ms 0x20

    rjmp theTop
