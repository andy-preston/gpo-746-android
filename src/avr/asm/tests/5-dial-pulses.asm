    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "dial.asm"
    .include "blinks.asm"

    ; Dial a digit and once the dial is fully returned the LED should flash
    ; ONCE the for the count of the digit you dialled (with "0" being 10)

    SetupOutputs
    Setup20msTimer
    SetupDial
    BlinkOff
checkDial:
    GetDialPulseCount
    tst _digit
    breq checkDial

    BlinkCount
    rjmp checkDial
