.macro SetupSerial
    ; Set the baud rate.
    ; Always set high before low! Writing UBRRL triggers an immediate
    ; update of the baud rate register.
    ; For @usartBaudRateRegister@ calculations, see:
    ;     src/buildSrc/src/main/kotlin/gpo_746/AvrConstants.kt
    ldi _io, high(@usartBaudRateRegister@)
    out UBRRH, _io
    ldi _io, low(@usartBaudRateRegister@)
    out UBRRL, _io
    
    ; Clear the transmssion complete flag
    ; Disable double speed mode(U2X) and
    ; multi-processor communication mode (MPCM)
    out UCSRA, _zero
    
    ; Disable interrupts and enable transmission (TX)
    ; Clear UCSZ2 and TXB8 as we are not using 9-bit transfer
    ldi _io, (1 << TXEN)
    out UCSRB, _io

    ; Clear USART mode (UMSEL) which sets asynchronous mode.
    ; Clear USART parity (UPM) which sets no parity
    ; Clear USART stop bits (USBS) which is one stop bit
    ; Set USART frame format bits to select 8 data bits
    ldi _io, (1 << UCSZ0) | (1 << UCSZ1)
    out UCSRC, _io
.endMacro


.macro ReadSerial
    ; ReadSerial isn't actually used anywhere in the code but it's been left
    ; here "for completness" and, I suppose, to blatantly ignore the YAGNI
    ; principle.

    ldi _digit, 0                          ; Return NULL if no data

    sbis UCSRA, RXC                        ; If data available skip the skip ;)
    rjmp finished                          ; Don't read if no data available
    
    in _digit, UDR                         ; Returns data or NULL in _digit
finished:
.endMacro


.macro WriteSerial
    ; This should only be called when there's specifically an ASCII digit to
    ; output in _digit. It doesn't do anything clever to ignore null values or
    ; anything like that.
bufferWait:
    sbis UCSRA, UDRE                       ; If buffer is empty, don't wait
    rjmp bufferWait

    out	UDR, _digit
.endMacro
