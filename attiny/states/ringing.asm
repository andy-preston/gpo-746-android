.macro RingState
    out outputPort, _zero           ; Clear the blinky
    AbortPulseCount                 ; Abandon any dial actions
    Ringing                         ; ring until hook is up
.endMacro
