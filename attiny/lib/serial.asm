 
.macro SetupSerial
    ldi _ioh, high(baudPrescale)         ; Set Baud rate.
    ldi _iol, low(baudPrescale)          ; Always set high before low!
    out UBRRH, _ioh                      ; Writing UBRRL triggers an immediate
    out UBRRL, _iol                      ; update of the baud rate prescaler.
    
    ldi _io, 0                     
    out UCSRA, _io                       ; Clear TXC. Disable U2X. Disable MPCM.

	ldi	_io, (1 << RXEN) | (1 << TXEN)   ; Disable Interrupts. Enable TX & RX.
	out	UCSRB, _io                       ; Clear UCSZ2 (No 9 bits). Clear TXB8.

    ldi _io, (1 << UCSZ0) | (1 << UCSZ1) ; UMSEL = Async. UPM = No parity.
    out USSRC, _io                       ; USBS = 1 Stop Bit. UCSZ1:0 = 8 bits.
.endmacro

.macro ReadSerial
    ldi _digit, 0                        ; Return NULL if no data
    sbis UCSRA, RXC                      ; If data available skip the skip ;)
    rjmp finished                        ; Don't read if no data available
    in _digit, UDR                       ; Returns data or NULL in _digit
finished:
.endmacro

.macro WriteSerialCharacter              ; expects char in _io
waitForUART:
    sbis UCSRA, UDRE                     ; If buffer is empty, don't wait
    rjmp waitForUART
    sts	UDR, _io
.endmacro
