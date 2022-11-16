.macro RingDelay
    out TCNT1H, _zero                ; before outputting the next byte
    out TCNT1L, _zero                ; zero the timer to wait
    ldi _io, (1 << OCF1A)            ; and clear the output compare flag
    out TIFR, _io                    ; TIFR (56) is out of range for using sbi
ringSeqWait:
    in _check, TIFR
    sbrs _check, OCF1A               ; if output compare is not yet triggered
    rjmp ringSeqWait                 ; ... wait for it to happen
.endMacro

.macro Ringing
    out outputPort, _zero            ; Clear the blinky
    AbortPulseCount
restartRingSequence:
    LoadZ ringSequence               ; ring sequence table in 20ms steps
getNextRingSeqByte:
    lpm _io, Z+
    sbrc _io, endDataFlag
    breq restartRingSequence
    RingDelay
    out outputPort, _io              ; flip the bells (or don't)
    SkipOnHook
    rjmp getNextRingSeqByte
.endMacro

    ; To set the pins to ring on:
    ; .equ ding = 1 << pinDing
    ; .equ dong = 1 << pinDong

    .equ endDataFlag = 7             ; dummy output bit used to make end of
    .equ endData = 1 << endDataFlag  ; ... sequence data

.macro RingData
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
