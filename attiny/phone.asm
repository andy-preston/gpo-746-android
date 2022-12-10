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
    SetRI                     ; Tell Android we're off hook
    ; ActivateAmplifier
    DialState                 ; Get and send a digit
    rjmp theBeggining

onHook:
    ResetRI                   ; Tell Android we're on hook
    ; DeactivateAmplifier
    SkipOnNoRTS               ; No incoming call?
    rjmp theBeggining

    Ringing                   ; Ring until pick up
    rjmp theBeggining

    .equ ding = 1 << pinDing
    .equ dong = 1 << pinDong
ringSequence:
    RingData