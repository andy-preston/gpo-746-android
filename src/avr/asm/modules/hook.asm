.macro PickedUp
    SendOffHookSignal
    ; ActivateAmplifier
.endMacro

.macro PutDown
    SendOnHookSignal
    ; DeactivateAmplifier
    AbortDialing
.endMacro
