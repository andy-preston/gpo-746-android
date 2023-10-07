# GPO 746 Telephone Linked to Android

An ATTiny2313 microcontroller hidden away in a "hacked" GPO-746 Telephone
paired up with an Android App that'll allow the GPO-746 to place and receive
calls on a mobile network.

Test framework an experimental Android/Kotlin driver for the CH340G USB/Serial
controller.

Docker container for the build environment
and another to produce the schematics.

## Conventions

Any commands given here that are prefixed with `./bin/` (e.g. `./bin/avrdude`)
are intended to be run from outside of the build container.

Any commands without that prefix should be run from inside the Docker container
which is entered with `./bin/builder`.

## Requirements

Many of the requirements are hidden away in the Docker containers but there's
still a couple of things needed on the outside.

### Docker

Obviously

### A Linux system (I use Ubuntu)

In theory at least, you can use some other
system to build this on but there's a bunch of
[scripts](https://github.com/andy-preston/gpo-746-android/tree/convert_it_all_to_kotlin/bin)
included in the build and testing process that assume Linux

### AVRDude

For loading the microcontroller code on the chip

## Build Environment

### Downloading The Android SDK

I haven't worked out how to include this in the automatic docker setup because
there's an "I accept the license terms" hoop to jump through which seems to
necessitate a manual install which is done once the Docker container is set up
with: `./bin/builder sdk`. This command only needs to be run once - unless
you delete the `./share/android` directory.

### One-Off Build

`./bin/builder build`

### Development Build

If you're hacking with the code and are going to be doing repeated builds,
it's better to have a shell prompt inside the container `./bin/builder`.
And to build from there with the `builder build` command.

This way, Gradle, will be able to do "all of it's stuff" with caching and
daemons, and get your incremental builds done faster (well, considering it
is Gradle, a little bit faster at least)

This command, inside the build container, lets you run any Gradle command
you like and a couple of extra shortcuts... Just run `builder` for help.

### Uploading Microcontroller Code

You can blow the fuses on the microcontroller with: `./bin/avrdude fuses`

And get a list of available HEX files (after a build) with: `./bin/avrdude`

And upload one of them with `./bin/avrdude {name}`

### Uploading Android App To Your Phone

`./bin/adb install`

### USB/Serial Testing

Some of the ATTiny test code requires a USB host to communicate with and it'd
be far too complicated, in my opinion, to do this with an Android device. So
there's a little test framework that runs on Linux to facilitate this.

Once the build is done, you can access this with `./bin/usb-test`

So as to not need constant `su` commands during testing, adding the user to the
`plugdev` group and creating `/etc/udev/rules.d/99-ch340g.rules` is advisable.

```text
SUBSYSTEM=="usb", ATTRS{idVendor}=="1a86", ATTRS{idProduct}=="7523", MODE="0660", GROUP="plugdev"
```

### Getting Gradle/Kotlin test reports

I don't like having to dig through the morass of Gradle build directories
looking for something useful. There's a script to find and display these:
`./bin/reports`

## Source code map

### src/android

Code directly related to the Android UI and APIs

### src/avr

Assembly code for the microcontroller

This uses
[GAVRAsm](http://www.avr-asm-tutorial.net/gavrasm/index_en.html)
which is included in the build container and uses Gradle just like
the Kotlin code.

### src/buildSrc

Kotlin code to pre-calculate some constants before the main build and insert
those constants into the assembly and Kotlin code prior to building.

Sort-of, kind-of like procedural macros in other languages but shoe-horned into
the Gradle/Kotlin ecosystem.

### src/shared/src/androidMain

(see also src/shared/src/androidUnitTest)

All of the Android app's internal classes and stuff that I've decoupled from
the Android UI

### src/shared/src/linuxMain

A test framework for the CH340G driver that can be run more comfortable on a
Linux command line without the complexities of trying to test and debug on an
Android device.

### src/shared/src/commonMain

( see also src/shared/src/commonTest)

Kotlin code shared between Linux and Android
