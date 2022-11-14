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

## Get the SDK

Because of annoying "You accepted the license" shenanigans, this can't be
part of the container build.

  ./builder sdk

## Building the Android app

  ./builder buildapp

## Running the Kotlin and Android tests

  ./builder testapp

## References

https://www.kuon.ch/post/2020-01-12-android-app/
