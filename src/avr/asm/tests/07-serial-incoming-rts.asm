    .device ATTiny2313
    .include "prelude.asm"
    .include "gpio.asm"

    ; When a connected serial device raises RTS, the LED should light and
    ; when the device lowers RTS the LED should go out.

the_top:
    sbis input_pins, pin_in_incoming_RTS
    rjmp no_incoming

incoming:
    sbi output_port, pin_out_LED
    rjmp the_top

no_incoming:
    cbi output_port, pin_out_LED
    rjmp the_top

