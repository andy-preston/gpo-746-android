    .device ATTiny2313

    ; I'm trying to arrange things so that each register has a single function
    ; so that on entering macros or subroutines, we don't need to concern
    ; ourselves with which registers we might be overwriting and saving
    ; precious stack space by not need to keep pushing and popping them.
    ; There's still quite a few registers free so this scheme can still be
    ; expanded quite a bit

    ; It must be all those MIPS book I've been reading but r0 is always zero
    .def _zero = r0

    ; Used in timer loops when we're checking some IO register for an
    ; "operation completed" flag
    .def _check = r1

    ; Used purely for the blink count test routine
    .def _blink_count = r2

    ; Used in delay functions to count the number of timer loops that have
    ; been completed so far
    .def _loops = r16

    ; For quick load immediate and store to IO register operations.
    .def _io =  r17

    ; Used solely in the phone bell routines for "command" bytes to write to
    ; the output GPIO port
    .def _bell = r18

    ; Used to store the dialled digit in the form of a pulse count or ASCII
    ; digit during the dial handling and serial output routines
    .def _digit = r19

    ; Always has the value of "0" in ASCII - used for conversion from number
    ; of pulses in the dial scan code to ASCII in the serial output code
    .def _asciiZero = r20

    ; I always need reminding about X, Y, and Z - they're the last ones
    ; to assign a more general purpose use to.
    ; r26 r27 - X
    ; r28 r29 - Y
    ; r30 r31 - Z


    .org 0x0000

    ; Standard Interrupt Vector Table taken from page 44 of
    ; https://ww1.microchip.com/downloads/en/DeviceDoc/Atmel-2543-AVR-ATtiny2313_Datasheet.pdf
    ; See also:
    ; http://www.avr-asm-tutorial.net/avr_en/interrupts/int_source.html
    rjmp progStart ; Reset Handler
    reti ; External Interrupt 0 Handler
    reti ; External Interrupt 1 Handler
    reti ; Timer 1 Capture Handler
    reti ; Timer 1 Compare A Handler
    reti ; Timer 1 Overflow Handler
    reti ; Timer 0 Overflow Handler
    reti ; USART 0 RX Complete Handler
    reti ; USART 0 UDR Empty Handler
    reti ; USART 0 TX Complete Handler
    reti ; Analog Comparator Handler
    reti ; Pin Change Interrupt
    reti ; Timer 1 Compare B Handler
    reti ; Timer 0 Compare A Handler
    reti ; Timer 0 Compare B Handler
    reti ; USI Start Handler
    reti ; USI Overflow Handler
    reti ; EEPROM Ready Handler
    reti ; Watchdog Overflow Handler


progStart:
    cli

    ; As far as I can remember, there's no subroutines or pushes anywhere.
    ; There's also, no static variables to get in the way of the default stack.
    ; But I'm still setting up the stack for completeness.
    ; There's no SPH to setup on the ATTiny2313 because it hasn't got enough
    ; RAM to need a high byte in it's stack pointer.
    ldi _io, RamEnd
    out SPL, _io

    ; This isn't MIPS, if I want to pretend I've got a "zero" register, I need
    ; to clear it before use.
    clr _zero


.macro LoadZ
    ; It's nice to be able to just use `LoadZ {address}` instead of having
    ; to remember the bit shift and assign each byte separately.
    ldi ZL, low(@0 << 1)
    ldi ZH, high(@0 << 1)
.endMacro
