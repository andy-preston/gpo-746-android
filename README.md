# GPO 746 Telephone Linked to Android

An ATTiny2313 microcontroller hidden away in a "hacked" GPO-746 Telephone
paired up with an Android App that'll allow the GPO-746 to place and receive
calls on a mobile network.

Test framework and experimental Android/Kotlin driver for the CH340G USB/Serial
controller.

Docker container for the build environment
and another to produce the schematics.

## Schematics

The schematics for the electronics can be found in `./schematic/schematic.html`
The Python code used to produce them is also to be found in this directory but
the HTML has been pre-built if you don't want to hack on them.

## Requirements

Many of the requirements are hidden away in the Docker containers but there's
still a couple of things needed on the outside.

### Docker

Obviously

### A Linux system (I use Ubuntu)

In theory at least, you can use some other
system to build this on but there's a bunch of
scripts in the top level `./bin/` directory that assume Linux.

### AVRDude

For loading the microcontroller code on the chip

## Build Environment

See the scripts and README.md in the `./bin/` directory.

## What's in the box

Things you might want to "steal" to use in your own projects?

### Custom CH340G driver

Android doesn't have drivers for serial/USB chips built in and there's a couple
of more general Java based drivers available on GitHub. But here, I'm using a
custom CH340G specific driver for a "special" use case - Using the handshaking
lines for GPIO. There's also a Linux/libusb implementation that I'm using for
testing.

The driver that's to be found in most systems uses the handshaking lines of the
CH340G for the purpose for which they're intended or ignores them completely.
But in here, we're using some of these lines as GPIO: R̅I̅ as an input and
R̅T̅S̅ and D̅T̅R̅ as outputs.
