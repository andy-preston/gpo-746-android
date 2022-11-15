all: blink5 blinkr serial hook active pulses ascii

blink5: tests/1-blink5.hex

blinkr: tests/2-blink-ring.hex

serial: tests/3-serial.hex

hook: tests/4-hook.hex

active: tests/5-dial-active.hex

pulses: tests/6-dial-pulses.hex

ascii: tests/7-dial-ascii.hex

modules/prescale.asm: prescale/*
	./bin/deno run prescale/calculate.js >modules/prescale.asm

%.hex: %.asm modules/*.asm modules/prescale.asm
	./bin/gavrasm -A -E -S -M $<
