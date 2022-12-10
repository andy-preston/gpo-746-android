# GPO 746 Telephone Linked to Android

An ATTiny2313 microcontroler hidden away in a "hacked" GPO-746 Telephone
paired up with an Android App that'll allow the GPO-746 to place and receive
calls on a mobile network.

Docker container for the Android build environment.

The ATTiny code needs GAVRAsm
<http://www.avr-asm-tutorial.net/gavrasm/index_en.html>

We also use Deno <https://deno.land/> to pre-calculate some constants before
assembling.

Both of these can be easily downloaded with the script in
`attiny/bin/get-binaries`

We also need GNU C to build a prototype CH340G controller.

Temporarily we need the Arduino environment to build a prototype of the
serial/USB microcontroller code... but this will end up in the assembler source
code before long.

## Build

Getting the Android SDK

```sh
make sdk
```

The Android app can be built or have it's test run with

```sh
make build
make test
```

The tests for the ATTiny code are run with. See Makefile for more targets.

```sh
make test1
```

## Uploading

```sh
bin/avrdude {target}
```

Where `{target}` is any Makefile target that produces a `.hex` file (or "fuses"
to set an ATTiny's fuses).

```sh
bin/adb-install
```

To upload a compiled app to an android device.

## References

https://www.kuon.ch/post/2020-01-12-android-app/
