; There's not a lot here but the hook switch is the simplest part of the whole
; thing.

; The actual reading of the hook switch's input bits are handled by macros
; in `gpio.asm` these are more like "event handlers" to be called when the
; hook switch changes state.

; I've even pre-empted future code with the macros to switch audio on and off
; that aren't even written yet - the electronics hasn't even been designed.

; `reset_or_abort_dialing` can be found in `dial-counter.asm`

.macro receiver_picked_up
    send_off_hook_signal
    ; ActivateAmplifier
    reset_or_abort_dialing
.endMacro

.macro receiver_put_down
    send_on_hook_signal
    ; DeactivateAmplifier
    reset_or_abort_dialing
.endMacro
