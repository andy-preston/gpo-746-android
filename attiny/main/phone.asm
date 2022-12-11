    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "attiny/modules/registers.asm"
    .include "attiny/modules/gpio.asm"
    .include "attiny/modules/prescale.asm"
    .include "attiny/modules/timer.asm"
    .include "attiny/modules/dial.asm"
    .include "attiny/modules/serial.asm"
    .include "attiny/modules/ring.asm"
    .include "attiny/states/dialing.asm"
    .include "attiny/states/pickup.asm"
    .include "attiny/states/putdown.asm"

progStart:
    SetupStackAndReg
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
