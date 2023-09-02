    .include "prelude.asm"
    .include "gpio.asm"

theTop:
    SkipOnNoIncoming
    rjmp incoming

noIncoming:
    BlinkOff
    rjmp theTop

incoming:
    BlinkOn
    rjmp theTop
