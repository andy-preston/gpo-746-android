.macro Ringing
    ; Step through the ringing sequence, outputting the next step each 20ms
    ; It will only "fall through" if the receiver is picked up.
    ; Whilst the sequence is playing all outputs on the port will go low
    ; which includes the diagnostic LED and the external "picked-up" signal
    ; to the Android device. The first is a little inconvenient the latter
    ; should be OK as long as we step out of the sequence before sending the
    ; output to the android device high.
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


    ; dummy output bit used to mark the end of the sequence data
    .equ endDataFlag = 7
    .equ endData = 1 << endDataFlag

    .equ emulatedRing = 1
    .equ realRing = 2


.macro RingData
    .if @0 == emulatedRing
        ; For testing purposes instead of outputting to the bell pins
        ; output the "ding" pulses to the diagnostic LED instead and
        ; set the "dong" pulses to 0 so that they'll be ignored
        .equ ding = 1 << pinBlink
        .equ dong = 0
    .else
        .equ ding = 1 << pinDing
        .equ dong = 1 << pinDong
    .endIf

    ; Original GPO phones have the ringer solenoids wired in such a way that
    ; they respond to AC pulses at 25Hz (half-period = 20mS).
    ; For this system, they have been wired separately to an IO pin and to
    ; ground so that they can easily be driven digitally.
    ; The traditional British ring cadence is
    ; 400mS ring, 200mS silence, 400mS ring, 2000mS silence (3 second total)

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
