test1: attiny/tests/1-blink5.hex

test2: attiny/tests/2-hook.hex

test3: attiny/tests/3-blink-ring.hex

test4: attiny/tests/4-dial-active.hex

test5: attiny/tests/5-dial-pulses.hex

test6: attiny/tests/6-serial-incoming-rts.hex

test7: attiny/tests/7-serial-hook-ri.hex

test8: attiny/tests/8-serial-send.hex

test9: attiny/tests/9-dial-serial.hex

attiny: attiny/main/phone.hex

attiny/modules/constants.asm: attiny/calculator/*
	./bin/deno task calc >attiny/modules/constants.asm

%.hex: %.asm attiny/modules/constants.asm attiny/modules/*.asm
	./bin/gavrasm -A -E -S -M $<

ch340g/libusb_test/driver_functions.c: ch340g/driver-spec/*
	./bin/deno task spec C >./ch340g/libusb_test/driver_functions.c

libusb: ch340g/libusb_test/*
	ch340g/libusb_test/make

android/app/src/main/java/com/gitlab/edgeeffect/gpo746/ch340g.kt:  ch340g/driver-spec/*
	./bin/deno task spec Kotlin >./android/app/src/main/java/com/gitlab/edgeeffect/gpo746/ch340g.kt

android:
	./bin/android-container build

test:
	./bin/android-container test

sdk:
	./bin/android-container sdk

clean:
	rm -rf `cat .gitignore` gradlew* .gitattributes
