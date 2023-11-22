.macro setup_serial
    ; Set the baud rate.
    ; Always set high before low! Writing UBRRL triggers an immediate
    ; update of the baud rate register.
    ; For @usart_baud_rate_register@ calculations, see:
    ;     src/buildSrc/src/main/kotlin/gpo746/AvrConstants.kt
    ldi _io, high(@usart_baud_rate_register@)
    out UBRRH, _io
    ldi _io, low(@usart_baud_rate_register@)
    out UBRRL, _io

    ; Clear the transmission complete flag
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


.macro write_serial
    ; This should only be called when there's specifically an ASCII digit to
    ; output in _dialled_digit. It doesn't do anything clever to ignore null
    ; values or anything like that.
buffer_wait:
    sbis UCSRA, UDRE                       ; If buffer is empty, don't wait
    rjmp buffer_wait

    out	UDR, _dialled_digit
.endMacro
