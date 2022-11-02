# CH340G Prototype Driver

## Rationale

In the various libraries I'm "drawing inspiration from", there's quite a bit of
contradiction as to the correct way to initialise a CH340 / CH341 chip and the
edit, compile, upload, test cycle for my Android app is just too complicated to
spark joy.

## What's In The Box

Yuxiang Zhang provided a nice Data Transfer Demo for libusb which has
similarities to the Android USB Manager that I'll have to connect to
eventually. And I modified this to give us the simplest, most stripped down
prototype that I could use to get things going with a nice quick workflow.

### Procedure

### Disable Linux Driver

So that we can play around with the settings of the CH340G without having the
Linux kernel driver getting in the way, I blacklisted that driver.

```bash
echo blacklist ch341 > /etc/modprobe.d/blacklist-ch341.conf
```

### Compile The Driver Prototype

This is just a simple single C file, and you can compile it with:

```bash
gcc ch340g.c `pkg-config libusb-1.0 --libs --cflags` -o ch340g
```

You'll either have to run this as `su` or sort out the privileges to run it.

## Other Chip Variants

I've stripped this prototype code down to the bare minimum to get the
**CH340G** working as I need it. If you're using other CH340 variants or this
CH341 that the Linux device driver seems to support (but I've never heard of)
then we may need to put back in some of the code that I've stripped out. If
you've got this working on other chips in the series, I'd love to hear from you.

## References

### Yuxiang Zhang's libusb CH340 Data Transfer Demo

https://gist.github.com/z4yx/8d9ecad151dad351fbbb

### Linux device driver for CH34x chips

https://github.com/lizard43/CH340G/blob/master/ch340g/ch34x.c

### felHR85 - Java Android driver for USB serial chips including the CH340 series

https://github.com/felHR85/UsbSerial/blob/7fff8b6d5ca19590dcb05c3f977970e8cce103b7/usbserial/src/main/java/com/felhr/usbserial/CH34xSerialDevice.java

### mik3y - Java Android driver for USB serial chips including the CH340 series

https://github.com/mik3y/usb-serial-for-android/blob/master/usbSerialForAndroid/src/main/java/com/hoho/android/usbserial/driver/Ch34xSerialDriver.java
