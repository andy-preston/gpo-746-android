    .include "prelude.asm"
    .include "gpio.asm"

    ; When a connected serial device raises DTR, the LED should light and
    ; when the device lowers DTR the LED should go out.

the_top:
    skip_on_no_amp_required
    rjmp amp_required

no_amp_required:
    blink_off
    rjmp the_top

amp_required:
    blink_on
    rjmp the_top
