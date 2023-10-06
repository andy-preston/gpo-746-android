.macro Ringing
restartRingSequence:
    LoadZ ringSequence                   ; ring sequence table in 20ms steps
getNextRingSeqByte:
    lpm _bell, Z+
    sbrc _bell, endDataFlag              ; skip restart if no endDataFlag
    rjmp restartRingSequence
    WaitForRingerTicks
    out outputPort, _bell                ; flip the bells (or don't)
    SkipOffHook
    rjmp getNextRingSeqByte
.endMacro

    .equ endDataFlag = 7                 ; dummy output bit used to make end of
    .equ endData = 1 << endDataFlag      ; ... sequence data
    .equ emulatedRing = 1
    .equ realRing = 2

.macro RingData
    .if @0 == emulatedRing
        .equ ding = 1 << pinBlink
        .equ dong = 0
    .else
        .equ ding = 1 << pinDing
        .equ dong = 1 << pinDong
    .endIf
    ; 400mS ring, 200mS silence, 400mS ring, 2000mS silence (3 second total)
    ; The ringing frequency of the bells is 25Hz (half-period = 20mS)
    .db ding, dong, ding, dong, ding, dong, ding, dong, ding, dong ; 20X20
    .db ding, dong, ding, dong, ding, dong, ding, dong, ding, dong ;      =400ms
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0 ; 10X20=200ms
    .db ding, dong, ding, dong, ding, dong, ding, dong, ding, dong ; 20X20
    .db ding, dong, ding, dong, ding, dong, ding, dong, ding, dong ;      =400ms
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0 ; 100X20
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0 ;     =2000ms
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db    0,    0,    0,    0,    0,    0,    0,    0,    0,    0
    .db endData, endData
.endMacro
