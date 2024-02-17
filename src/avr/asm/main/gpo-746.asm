    .include "prelude.asm"
    .include "gpio.asm"
    .include "ring-timer.asm"
    .include "ring-sequence.asm"
    .include "ring-state.asm"
    .include "dial-counter.asm"
    .include "dial-ascii.asm"
    .include "serial.asm"


    setup_outputs
    setup_timer
    setup_dial
    setup_serial
    setup_ascii
    setup_ring_sequence


ring_for_incoming_call:
    skip_on_incoming
    rjmp control_amplifier

ring_prepare:
    set_timer_interval_to_20ms
    start_interval_timer
    load_ring_sequence_byte

control_amplifier:
    skip_on_no_amp_required
    rjmp amp_required

no_amp_required:
    switch_amp_off
    rjmp hook_and_dial

amp_required:
    switch_amp_on

hook_and_dial:
    skip_when_picked_up
    rjmp put_down

picked_up:
    send_picked_up_signal

    get_dial_pulse_count
    convert_pulse_count_to_ascii
    write_serial

    rjmp ring_for_incoming_call

put_down:
    send_put_down_signal
    rjmp ring_for_incoming_call

ring_sequence:
    ring_data real_ring
