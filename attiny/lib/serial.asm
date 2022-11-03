.macro SetupSerial
    ldi _io, low(baudPrescale)
    out	UBRRL, _io
    ldi _io, high(baudPrescale)
	out	UBRRH, _io
	ldi	_io, (1 << RXEN) | (1 << TXEN)
	out	UCSRB, _io
.endmacro

.macro ReadSerial
    sbis UCSRA, RXC
    rjmp skipReadSerial
    in _digit, UDR
    rjmp finishedReadSerial
skipReadSerial:
    ldi _digit, 0
finishedReadSerial:
.endmacro

.macro WriteSerialCharacter ; expects char in _io
waitForUART:
    sbic UCSRA, UDRE
    rjmp waitForUART
    sts	UDR, _io
.endmacro
