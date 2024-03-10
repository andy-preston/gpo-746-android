.macro Hook_And_Dial
    skip_when_picked_up
    rjmp put_down

    send_picked_up_signal
    get_dial_pulse_count
    tst _dialled_digit
    breq end

    convert_pulse_count_to_ascii
    write_serial
    rjmp end

put_down:
    send_put_down_signal
    clr _dialled_digit
    clr _pulse_counter

end:
.endMacro
