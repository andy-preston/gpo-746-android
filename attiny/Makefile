all: blink serial hook active pulse

blink: tests/1-blink.hex

serial: tests/2-serial.hex

hook: tests/3-hook.hex

active: tests/4-dial-active.hex

pulse: tests/5-dial-pulses.hex

lib/prescale.asm: prescale/*
	./bin/deno run prescale/calculate.js >lib/prescale.asm

%.hex: %.asm lib/prescale.asm lib/*.asm
	./bin/gavrasm -A -E -S -M $<
