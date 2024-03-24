; These "clever" blinks are here exclusively for diagnostics during testing.
; Any use of the LED in production code is limited to a straight "blink on" or
; "blink off"

.macro blink_flip
    ; If the LED bit is 0 (off)
    sbic output_port, pin_out_LED
    rjmp blink_off

    ; Switch it on
    sbi output_port, pin_out_LED
    rjmp blink_end

    ; if it was on, switch it off
blink_off:
    cbi output_port, pin_out_LED

blink_end:
.endMacro



.macro blink_count
    ; Flash the LED, the number of times indicated by _dialled_digit

    mov _blink_count, _dialled_digit

blink_loop:
    ; If the count has reached zero, skip to the end
    tst _blink_count
    breq blink_end

    sbi output_port, pin_out_LED
    wait_for_fifth_of_a_second
    cbi output_port, pin_out_LED
    wait_for_fifth_of_a_second

    dec _blink_count
    rjmp blink_loop

blink_end:
    wait_for_half_a_second
.endMacro
