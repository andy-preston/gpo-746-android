    .device ATTiny2313

    .org 0x0000   ; reset vector
    rjmp progStart

    .org 0x003E
    .include "modules/registers.asm"
    .include "modules/gpio.asm"
    .include "modules/prescale.asm"
    .include "modules/timer.asm"
    .include "modules/dial.asm"
    .include "modules/serial.asm"
    .include "states/dialing.asm"
    .include "states/ringing.asm"

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
    SendOffHookSignal
    ; ActivateAmplifier
    GetAndSendADigit
    rjmp theBeggining

onHook:
    SendOnHookSignal
    ; DeactivateAmplifier
    SkipOnNoIncoming
    rjmp theBeggining

    RingUntilPickedUp
    rjmp theBeggining

    .equ ding = 1 << pinDing
    .equ dong = 1 << pinDong
ringSequence:
    RingData