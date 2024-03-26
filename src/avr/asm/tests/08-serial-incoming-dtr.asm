    .device ATTiny2313
    .include "prelude.asm"
    .include "gpio.asm"

    ; When a connected serial device raises DTR, the LED should light and
    ; when the device lowers DTR the LED should go out.

the_top:
    sbic input_pins, pin_in_amp_required_DTR
    rjmp amp_required

no_amp_required:
    cbi output_port, pin_out_LED
    rjmp the_top

amp_required:
    sbi output_port, pin_out_LED
    rjmp the_top
