; These "clever" blinks are here exclusively for diagnostics during testing.
; Any use of the LED in production code is limited to the blink_on / blink_off
; macros in `gpio.asm`

.macro blink_flip
    ; If the LED bit is 0 (off)
    sbic output_port, pin_out_LED
    rjmp blink_off

    ; Switch it on
    blink_on
    rjmp blink_end

    ; if it was on, switch it off
blink_off:
    blink_off

blink_end:
.endMacro



.macro blink_count
    ; Flash the LED, the number of times indicated by _dialled_digit

    mov _blink_count, _dialled_digit

blink_loop:
    ; If the count has reached zero, skip to the end
    tst _blink_count
    breq blink_end

    blink_on
    wait_for_multiple_20ms 0x10
    blink_off
    wait_for_multiple_20ms 0x10

    dec _blink_count
    rjmp blink_loop

blink_end:
    wait_for_multiple_20ms 0x20
.endMacro
