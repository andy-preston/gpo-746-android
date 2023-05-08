 .macro SetupSerial
    ldi _io, high(baudPrescale)          ; Set Baud rate.
    out UBRRH, _io                       ; Always set high before low!
    ldi _io, low(baudPrescale)           ; Writing UBRRL triggers an immediate
    out UBRRL, _io                       ; update of the baud rate prescale.
    ldi _io, 0                           ; Clear TXC.
    out UCSRA, _io                       ; Disable U2X & MPCM.
    ldi	_io, (1 << TXEN)                 ; Disable Interrupts. Enable TX.
    out	UCSRB, _io                       ; Clear UCSZ2 (No 9 bits). Clear TXB8.
    ldi _io, (1 << UCSZ0) | (1 << UCSZ1) ; UMSEL = Async. UPM = No parity.
    out UCSRC, _io                       ; USBS = 1 Stop Bit. UCSZ1:0 = 8 bits.
.endMacro

.macro ReadSerial
    ldi _digit, 0                        ; Return NULL if no data
    sbis UCSRA, RXC                      ; If data available skip the skip ;)
    rjmp finished                        ; Don't read if no data available
    in _digit, UDR                       ; Returns data or NULL in _digit
finished:
.endMacro

.macro WriteSerial                       ; expects char in _digit
bufferWait:
    sbis UCSRA, UDRE                     ; If buffer is empty, don't wait
    rjmp bufferWait
    out	UDR, _digit
.endMacro
