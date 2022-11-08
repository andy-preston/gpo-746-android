all: tests/1-blink.hex tests/2-serial.hex

lib/prescale.asm: prescale/*
	./bin/deno run prescale/calculate.js >lib/prescale.asm

%.hex: %.asm lib/prescale.asm lib/*.asm
	./bin/gavrasm -A -E -S -M $<
