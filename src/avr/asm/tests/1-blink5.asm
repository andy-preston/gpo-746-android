    .include "prelude.asm"
    .include "gpio.asm"
    .include "timer.asm"
    .include "blinks.asm"

    ; A Very simple test which will blink the LED 5 times with a 200ms delay,
    ; wait 500ms and blink it 5 times again... ad infinitum.

    SetupOutputs
    Setup20msTimer
    ldi _digit, 5
loop:
    BlinkCount
    rjmp loop
