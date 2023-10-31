    .include "prelude.asm"
    .include "gpio.asm"

    ; When a connected serial device raises RTS, the LED should light and
    ; when the device lowers RTS the LED should go out.

the_top:
    skip_on_no_incoming
    rjmp incoming

no_incoming:
    blink_off
    rjmp the_top

incoming:
    blink_on
    rjmp the_top
