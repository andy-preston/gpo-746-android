    ; cpuFrequency and timer 1 parameters are calculated by makefile
    ; we can't do it here because we need a real (not integer) divide
    .include "attiny/modules/timer1_prescalar.asm"

    .if ringerTicks < 1
        .error "ringerTicks too small"
    .endif

    .if ringerTicks > 0xffff
        .error "ringerTicks too big"
    .endif

    .if timer1PrescalarValue == 0
        .equ timer1Prescalar = (1 << CS10)
    .endif
    .if timer1PrescalarValue == 8
        .equ timer1Prescalar = (1 << CS11)
    .endif
    .if timer1PrescalarValue == 64
        .equ timer1Prescalar = (1 << CS11) | (1 << CS10)
    .endif
    .if timer1PrescalarValue == 256
        .equ timer1Prescalar = (1 << CS12)
    .endif
    .if timer1PrescalarValue == 1024
        .equ timer1Prescalar = (1 << CS12) | (1 << CS10)
    .endif
    .ifndef timer1Prescalar
        .error "timer1PrescalerValue out of range"
    .endif

    .equ baudRate = 9600
    .equ baudMultiplier = baudRate * 16
    .equ baudPrescalar = (cpuFrequency / baudMultiplier) - 1
    .equ derivedBaudRate = cpuFrequency / (16 * (baudPrescalar + 1))
    .if derivedBaudRate != baudRate
        .error "Imperfect baud rate"
    .endif
