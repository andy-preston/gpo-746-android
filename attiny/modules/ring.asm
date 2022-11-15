    .equ endDataFlag = 7             ; dummy output bit used to make end of
    .equ endData = 1 << endDataFlag  ; ... sequence data

    .equ ding = 1 << pinDing
    .equ dong = 1 << pinDong

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
