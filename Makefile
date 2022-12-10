test1: attiny/tests/1-blink5.hex

test2: attiny/tests/2-hook.hex

test3: attiny/tests/3-blink-ring.hex

test4: attiny/tests/4-dial-active.hex

test5: attiny/tests/5-dial-pulses.hex

test6: attiny/tests/6-serial.hex

test7: attiny/tests/7-dial-ascii.hex

attiny: attiny/main/phone.hex

attiny/modules/prescale.asm: attiny/prescale/*
	./bin/deno run attiny/prescale/calculate.js >attiny/modules/prescale.asm

%.hex: %.asm attiny/modules/prescale.asm attiny/modules/*.asm
	./bin/gavrasm -A -E -S -M $<

sdk:
	./bin/android-container sdk

build:
	./bin/android-container build

test:
	./bin/android-container test

clean:
	rm -rf `cat .gitignore` gradlew* .gitattributes

