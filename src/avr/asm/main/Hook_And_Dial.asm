.macro Hook_And_Dial
    sbis input_pins, pin_in_receiver
    rjmp put_down

    sbi output_port, pin_out_pick_up_RI
    get_dial_pulse_count
    tst _dialled_digit
    breq end

    convert_pulse_count_to_ascii
    write_serial
    rjmp end

put_down:
    cbi output_port, pin_out_pick_up_RI
    clr _dialled_digit
    clr _pulse_count

end:
.endMacro
