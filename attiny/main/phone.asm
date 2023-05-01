    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/constants.asm"
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

theBeginning:
    SkipOffHook
    rjmp onHook

offHook:
    PickedUp
    GetAndSendADigit
    rjmp theBeggining

onHook:
    PutDown
    SkipOnNoIncoming
    rjmp theBeginning

    Ringing
    rjmp theBeginning

ringSequence:
    RingData realRing
