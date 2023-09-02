.macro BlinkFlip
    sbic outputPort, pinBlink            ; If LED is clear/off
    rjmp blinkOff                        ; don't switch it off
    BlinkOn                              ; switch it on instead
    rjmp blinkEnd
blinkOff:
    BlinkOff                             ; If it was set/on, switch it off
blinkEnd:
.endMacro

.macro BlinkCount                        ; blink the number of times in _digit
blinkLoop:
    tst _digit                           ; If there's nothing left to count
    breq blinkEnd                        ; There's nothing to show!
    BlinkOn
    TestDelay 0x10
    BlinkOff
    TestDelay 0x10
    dec _digit
    rjmp blinkLoop
blinkEnd:
    TestDelay 0x20
.endMacro
