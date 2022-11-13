.macro blinkCount
blinkLoop:
    tst _count                   ; If there's nothing left to count
    breq blinkEnd                ; There's nothing to show!
    BlinkOn
    TestDelay 0x10
    BlinkOff
    TestDelay 0x10
    dec _count
    rjmp blinkLoop
blinkEnd:
    TestDelay 0x20
.endMacro
