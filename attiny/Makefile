all: blink serial hook active pulses ascii

blink: tests/1-blink.hex

serial: tests/2-serial.hex

hook: tests/3-hook.hex

active: tests/4-dial-active.hex

pulses: tests/5-dial-pulses.hex

ascii: tests/6-dial-ascii.hex

modules/prescale.asm: prescale/*
	./bin/deno run prescale/calculate.js >modules/prescale.asm

%.hex: %.asm modules/*.asm
	./bin/gavrasm -A -E -S -M $<
