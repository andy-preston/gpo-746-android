# Wrapper scripts in the `./bin/` directory

## `adb`

Wraps the command with the same name from the Android build tools

`./bin/adb/install` loads the development version of the Android app to a
phone connected by USB

`./bin/adb/log` displays the entries in the log file on the USB attached
Android phone that are tagged with "gpo746"

You can also pass it any parameters that are accepted by the real `adb`
command

## `avrdude`

If you run it without any parameters, it'll give you a list of `.hex` files
that can be uploaded to the microcontroller.

`./bin/avrdude {hex file}` Load one of the `.hex` files listed in the previous
step to the microcontroller.

`./bin/avrdude fuses` program the ATTiny2313 fuses with the default values
used by this project.

By default, this is set up to use a networked stk500v1 programmer. You'll
almost certainly need to modify this and set the `PROGRAMMER` variable to a
suitable value for your setup. There's a couple of commented-out values as
examples.

## `gradle`

This launches a Docker container with `gradle` and other build tools.

`./bin/gradle` Will run the container with a shell that you can enter
further `gradle` commands into.

`./bin/gradle sdk` outside the container or `gradle sdk` inside the container
will download the required files for the Android SDK. You will need to do this
before the Android app can be built. This step couldn't be included in the
docker container because it includes "I accept the license (Y/N)"
shenanigans.

`./bin/gradle pretest` outside the container or `gradle pretest` inside the
container will run the tests for the `buildSrc` sub-project.

`./bin/gradle build` outside the container or `gradle build` inside the
container will build the Android app and all the microcontroller
test or application firmwares.

You can also run any parameters the the normal gradle command would accept.

If you only need to run one command the "outside the container" option is
probably easier. But if you're going to run multiple or repeated commands
(if you're debugging, there'll be many `gradle build` steps) it'd be better
to run the container with a shell and use the "inside the container" options.

## `reports`

After a build has completed, this will open the reports from the unit tests,
linters, etc in your chromium browser.

## `schematic`

A different Docker container for creating the `schematic.html` file.

If you don't modify the schematic, you won't need this as a copy of this file
is also included in the repository.

## `usb-test`

As part of the Android build, a Linux x86 native application for hardware
testing is also compiled.

This won't work until there's been a successful build.

So as to not need constant `su` commands during testing, adding the user to the
`plugdev` group and creating `/etc/udev/rules.d/99-ch340g.rules` is advisable.

```text
SUBSYSTEM=="usb", ATTRS{idVendor}=="1a86", ATTRS{idProduct}=="7523", MODE="0660", GROUP="plugdev"
```

`./bin/usb-test [test case] [number to repeat] [pause between tests]` will run
a test case. The default number of repeats is 1 and the default pause is 10
seconds.

The available test cases are:

`serial` - read the serial port and display results

`rts` - flip the current state of the RTS line

`dtr` - flip the current state of the DTR line

`ri` - display the current state of the RI line
