all: 1 2 3 4 5 6 7

1: tests/1-blink5.hex

2: tests/2-blink-ring.hex

3: tests/3-hook.hex

4: tests/4-dial-active.hex

5: tests/5-dial-pulses.hex

6: tests/6-serial.hex

7: tests/7-dial-ascii.hex

modules/prescale.asm: prescale/*
	./bin/deno run prescale/calculate.js >modules/prescale.asm

%.hex: %.asm modules/prescale.asm modules/*.asm
	./bin/gavrasm -A -E -S -M $<
