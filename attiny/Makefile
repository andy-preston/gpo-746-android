all: lib/prescale.asm

lib/prescale.asm: prescale/*
	/usr/local/bin/deno run prescale/calculate.js >lib/prescale.asm
