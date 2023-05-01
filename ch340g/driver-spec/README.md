# CH340G Driver Generator

You might find this "just a tad" over-engineered.

It's what you can expect from part-time projects - they'll either be woefully under-engineered
or ridiculously over-engineered... that's just the way it is. ;)

But after a few issues, I wanted to make sure that my C and `libusb`
[test CH340G driver](https://github.com/andy-preston/gpo-746-android/tree/android_ch340g_driver/ch340g/libusb_test)
and the Kotlin, Android final version were absolutely identical (well, as identical as possible).

I also used this as an exercise to learn TypeScript.

## Running it

The easiest thing to do is just use
[the main makefile](https://github.com/andy-preston/gpo-746-android/blob/android_ch340g_driver/Makefile)

But if you need to tweak the driver, then
[spec.ts](https://github.com/andy-preston/gpo-746-android/blob/android_ch340g_driver/ch340g/driver-spec/spec.ts)
is the place to look.

## What's in the box

* `baud.js` for calculating prescale, divider, mod for setting baud rate
* `tests/baud_test.ts` check calculated baud figures against original lookup tables
* `register.js` specs for the CH340G registers
* `request.js` usb request names and codes
* `spec.ts` the spec for the driver methods in a language agnostic sort-of DSL
* `generator.ts` a façade on the generator code so that you can make
a nice tidy spec in `spec.ts` using `lnguage_*.ts`
* `language_module.ts` the basic interface that `language_c.ts`
and `language_kotlin.ts` implement
* `language_c.ts` an instance of `language_module.ts` to
generate code in C for `libusb`
* `language_kotlin.ts` (doesn't exist yet) an instance of `language_module.ts`
to generate Kotlin code for the Android app
* `hex.js` stuff for converting and checking hexadecimal numbers
