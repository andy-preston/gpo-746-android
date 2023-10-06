    .include "prelude.asm"
    .include "gpio.asm"
    .include "constants.asm"
    .include "timer.asm"
    .include "ring.asm"
    .include "dial.asm"
    .include "serial.asm"
    .include "dial-serial.asm"
    .include "hook.asm"

    SetupOutputs
    SetupTimer
    SetupDial
    SetupSerial

theBeginning:
    SkipOffHook
    rjmp onHook

offHook:
    PickedUp
    GetAndSendADigit
    rjmp theBeginning

onHook:
    PutDown
    SkipOnNoIncoming
    rjmp theBeginning

    Ringing
    rjmp theBeginning

ringSequence:
    RingData realRing
