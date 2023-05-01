    .include "attiny/modules/prelude.asm"
    .include "attiny/modules/gpio.asm"

theTop:
    SkipOnNoIncoming
    rjmp incoming

noIncoming:
    BlinkOff
    rjmp theTop

incoming:
    BlinkOn
    rjmp theTop
