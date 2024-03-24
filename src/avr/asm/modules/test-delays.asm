; Delay loops for test procedures only
; also need to include "timer.asm"

.macro wait_for_20ms_interval
    ; Wait for timer1_ring_ticks ticks to complete
wait_for_timer:
    in _timer_wait, TIFR
    sbrs _timer_wait, ring_interval
    rjmp wait_for_timer
.endMacro


; The following `wait_for_XXX` macros naming specific time intervals are only
; for use in testing where timer1_ring_ticks is too short for a human to
; notice. The timing values aren't particularly accurate because we're ignoring
; the few cycles it takes to set up the timer before it runs. But it's close
; enough for testing. Also some of the code winds up being a tad inefficient.
; But, again, that's no big deal during testing. I wouldn't like that sort of
; thing in "production" though.


.macro wait_for_20ms
    start_interval_timers
    wait_for_20ms_interval
.endMacro


.macro wait_for_multiple_20ms
    ; Used in tests . Because we're re-using a delay loop with a specific purpose just
    ; to get a human visible delay, the timing is a bit odd here:
    ; The parameter is duration with a value of 50 indicating 1 second
    ; (Plus a little bit extra as we're ignoring the time it takes the loop
    ; itself to execute)
    ldi _delay_repeat, @0
delay:
    wait_for_20ms
    dec _delay_repeat
    brne delay
.endMacro


.macro wait_for_fifth_of_a_second
    wait_for_multiple_20ms 10
.endMacro


.macro wait_for_half_a_second
    wait_for_multiple_20ms 25
.endMacro


.macro wait_for_a_second
    wait_for_multiple_20ms 50
.endMacro


.macro wait_for_two_seconds
    wait_for_multiple_20ms 100
.endMacro


.macro wait_for_three_seconds
    wait_for_multiple_20ms 150
.endMacro


.macro wait_for_four_seconds
    wait_for_multiple_20ms 200
.endMacro


.macro wait_for_five_seconds
    wait_for_multiple_20ms 250
.endMacro
