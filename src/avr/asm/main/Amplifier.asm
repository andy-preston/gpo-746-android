.macro Amplifier
    skip_on_no_amp_required
    rjmp amp_required

    switch_amp_off
    rjmp end

amp_required:
    switch_amp_on
end:
.endMacro