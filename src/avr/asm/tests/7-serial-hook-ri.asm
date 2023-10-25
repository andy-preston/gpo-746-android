    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"

    ; Will send a high RI signal for approximately 400ms and then a low RI
    ; signal for approximately 400ms, the remote serial device should be able
    ; to detect this.

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
