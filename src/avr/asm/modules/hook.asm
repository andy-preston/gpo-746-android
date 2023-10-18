; There's not a lot here but the hook switch is the simplest part of the whole
; thing.

; The actual reading of the hook switch's input bits are handled by macros
; in `gpio.asm` these are more like "event handlers" to be called when the
; hook switch changes state.

; I've even pre-empted future code with the macros to switch audio on and off
; that aren't even written yet - the electronics hasn't even been designed.

; `AbortDialing` can be found in `dial.asm`

.macro PickedUp
    SendOffHookSignal
    ; ActivateAmplifier
    ResetOrAbortDialing
.endMacro

.macro PutDown
    SendOnHookSignal
    ; DeactivateAmplifier
    ResetOrAbortDialing
.endMacro
