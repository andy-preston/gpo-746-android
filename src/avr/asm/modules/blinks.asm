; These "clever" blinks are here exclusively for diagnostics during testing.
; Any use of the LED in production code is limited to the BlinkOn / BlinkOff
; macros in `gpio.asm`

.macro BlinkFlip
    ; If the LED bit is 0 (off)
    sbic outputPort, pinBlink
    rjmp blinkOff

    ; Switch it on
    BlinkOn
    rjmp blinkEnd

    ; if it was on, switch it off
blinkOff:
    BlinkOff

blinkEnd:
.endMacro



.macro BlinkCount
    ; Flash the LED, the number of times indicated by _digit

    mov _blink_count, _digit

blinkLoop:
    ; If the count has reached zero, skip to the end
    tst _blink_count
    breq blinkEnd

    BlinkOn
    WaitForMultiple20ms 0x10
    BlinkOff
    WaitForMultiple20ms 0x10

    dec _blink_count
    rjmp blinkLoop

blinkEnd:
    WaitForMultiple20ms 0x20
.endMacro
