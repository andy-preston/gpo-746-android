    .include "prelude.asm"
    .include "timer.asm"
    .include "serial.asm"
    .include "gpio.asm"
    .include "blinks.asm"

    ; Continuously sends the given string across the serial port which should be
    ; detected by the attached device.

    Setup20msTimer
    SetupSerial

theTop:
    ; lpm always uses the Z-Register and never X and Y
    ; It's OK for us to have a table in program memory here
    ; But, in "production", the Z-Register is used to hold our current
    ; position in the ring sequence and any other use of Z will clash with that.
    LoadZ digitsToSend
    BlinkFlip
sendDigit:
    WaitForMultiple20ms 0x01
    lpm _digit, Z+
    cpi _digit, ' '
    breq theTop
    WriteSerial
    rjmp sendDigit

digitsToSend:
    .db "Testing1234 "