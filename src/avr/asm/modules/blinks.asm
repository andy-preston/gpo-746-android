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
    ; _digit will be zero by the time this routine finishes but it is only for
    ; use in the testing phase.
blinkLoop:
    ; If the count has reached zero, skip to the end
    tst _digit
    breq blinkEnd
    
    BlinkOn
    TestDelay 0x10
    BlinkOff
    TestDelay 0x10
    
    dec _digit
    rjmp blinkLoop

blinkEnd:
    TestDelay 0x20
.endMacro
