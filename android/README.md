# CH340G driver

It's "not natural" to roll your own library when there's a perfectly good one
available! And, here I am, doing just that thing! But I wanted something nicely
difficult to do to help me learn Kotlin and here I am.

I'm basing this on the Linux device driver for ch34x chips at:
<https://github.com/lizard43/CH340G/blob/master/ch340g/ch34x.c>
and two Android Java libraries by
[felHR85](https://github.com/felHR85/UsbSerial/blob/7fff8b6d5ca19590dcb05c3f977970e8cce103b7/usbserial/src/main/java/com/felhr/usbserial/CH34xSerialDevice.java)
and
[mik3y](https://github.com/mik3y/usb-serial-for-android/blob/master/usbSerialForAndroid/src/main/java/com/hoho/android/usbserial/driver/Ch34xSerialDriver.java)

The previous implementations aren't particularly self-documented, which I'm
trying to improve on here. Although I've still got a few "horribles" where I
don't yet understand what my source materials are doing.

I'm also trying to use the strong typing offered by Kotlin to "Make Invalid
States Unrepresentable"

This is a work in progress - expect holes, inconsistencies and mistakes!
