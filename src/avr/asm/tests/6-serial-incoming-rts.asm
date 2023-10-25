    .include "prelude.asm"
    .include "gpio.asm"

    ; When a connected serial device raises RTS, the LED should light and
    ; when the device lowers RTS the LED should go out.

theTop:
    SkipOnNoIncoming
    rjmp incoming

noIncoming:
    BlinkOff
    rjmp theTop

incoming:
    BlinkOn
    rjmp theTop
