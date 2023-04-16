test1: attiny/tests/1-blink5.hex

test2: attiny/tests/2-hook.hex

test3: attiny/tests/3-blink-ring.hex

test4: attiny/tests/4-dial-active.hex

test5: attiny/tests/5-dial-pulses.hex

test6: attiny/tests/6-serial-raspberry.hex

test7: attiny/tests/7-dial-ascii.hex

test8: attiny/tests/8-simple-serial.hex

attiny: attiny/main/phone.hex

lint_ts:
	./bin/deno lint attiny/calculator/* ch340g/driver-spec/*

attiny/modules/constants.asm: ./bin/deno run attiny/calculator/*
	./bin/deno run attiny/calculator/calculator.ts >attiny/modules/constants.asm

%.hex: %.asm attiny/modules/constants.asm attiny/modules/*.asm
	./bin/gavrasm -A -E -S -M $<

ch340g/libusb_test/driver_functions.c: ch340g/driver-spec/*
	./bin/deno run --allow-read \
		./ch340g/driver-spec/spec.ts >./ch340g/libusb_test/driver_functions.c

sdk:
	./bin/android-container sdk

build:
	./bin/android-container build

test:
	./bin/android-container test

clean:
	rm -rf `cat .gitignore` gradlew* .gitattributes

