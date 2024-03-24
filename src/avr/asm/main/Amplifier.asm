.macro Amplifier
    sbic input_pins, pin_in_amp_required_DTR
    rjmp amp_required

    cbi output_port, pin_out_amplifier
    rjmp end

amp_required:
    sbi output_port, pin_out_amplifier
end:
.endMacro