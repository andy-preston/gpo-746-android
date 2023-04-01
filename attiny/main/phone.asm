    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/precompiled.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/ring.asm"
    .include "attiny/modules/dial.asm"
    .include "attiny/modules/serial.asm"
    .include "attiny/modules/dial-serial.asm"
    .include "attiny/modules/hook.asm"

    SetupOutputs
    SetupTimer
    SetupDial
    SetupSerial

theBeggining:
    SkipOffHook
    rjmp onHook

offHook:
    PickedUp
    GetAndSendADigit
    rjmp theBeggining

onHook:
    PutDown
    SkipOnNoIncomming
    rjmp theBeggining

    Ringing
    rjmp theBeggining

ringSequence:
    RingData realRing
