test1: attiny/tests/1-blink5.hex

test2: attiny/tests/2-hook.hex

test3: attiny/tests/3-blink-ring.hex

test4: attiny/tests/4-dial-active.hex

test5: attiny/tests/5-dial-pulses.hex

test6: attiny/tests/6-serial.hex

test7: attiny/tests/7-dial-ascii.hex

attiny: attiny/main/phone.hex

CPU_FREQUENCY = 14745600
RING_HALF_PERIOD = 20
TIMER1_PRESCALAR_VALUE = 256
TIMER1_FREQUENCY = ${shell echo $(CPU_FREQUENCY) / $(TIMER1_PRESCALAR_VALUE) | bc}
TIMER1_PERIOD = ${shell echo 1 / $(TIMER1_FREQUENCY) | bc -l}
TIMER1_TICK=${shell echo ${TIMER1_PERIOD} \* 1000 | bc -l}
RINGER_TICKS=${shell echo ${RING_HALF_PERIOD} / ${TIMER1_TICK} | bc}

attiny/modules/timer1_prescalar.asm: Makefile
	${shell echo "\
    .equ cpuFrequency = $(CPU_FREQUENCY)\n\
    .equ timer1PrescalarValue = $(TIMER1_PRESCALAR_VALUE)\n\
    ; ringHalfPeriod = $(RING_HALF_PERIOD)\n\
    ; timer1Frequency = $(TIMER1_FREQUENCY) ; cpuFrequency / timer1Prescalar\n\
    ; timer1Period = $(TIMER1_PERIOD) ; 1 / timer1Frequency\n\
    ; timer1Tick = $(TIMER1_TICK) ; tmerPeriod * 1000\n\
    .equ ringerTicks = $(RINGER_TICKS) ; ringHalfPeriod / timer1Tick"\
	> attiny/modules/timer1_prescalar.asm}

%.hex: %.asm attiny/modules/timer1_prescalar.asm attiny/modules/*.asm
	./bin/gavrasm -A -E -S -M $<

sdk:
	./bin/android-container sdk

build:
	./bin/android-container build

test:
	./bin/android-container test

clean:
	rm -rf `cat .gitignore` gradlew* .gitattributes

