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

## Procedure

### LibUsb 1.0

```bash
sudo apt-get install libusb-1.0-0-dev
```

### Disable Linux Driver

So that we can play around with the settings of the CH340G without having the
Linux kernel driver getting in the way, I blacklisted that driver.

```bash
echo blacklist ch341 > /etc/modprobe.d/blacklist-ch341.conf
```

### Compile The Driver Prototype

This is just a simple single C file, and you can compile it with:

```bash
gcc libusb_test.c $(pkg-config libusb-1.0 --libs --cflags) -o libusb_test
```

### Running The Test

You'll either have to run this as `su` or sort out the privileges to run it.

(And if you do sort out the privileges to run it as a normal user...
could you tell me what they are)

### State of flux

I'm currently working on streamlining the test and how it corresponds to the
final Android app... these instructions are currently quite shonkey.

### CH340G Board To Test Against

This can either be connected to the board described in this project which should
be running the code in:
https://gitlab.com/edgeeffect/gpo-746-android/-/blob/master/attiny/tests/2-serial.asm

```lang-none

+----------------------------+      +--------------+
| Test Board or Arduino Nano |      | Raspberry Pi |
|                            |      |              |
| +-----+        +--------+  |      |              |
| |     |--TX--->|        |         |              |
| | AVR |--RTS-->| CH340G |<--USB-->|              |
| |     |--RI--->|        |         |              |
| +-----+        +--------+  |      |              |
|                            |      |              |
+----------------------------+      +--------------+

```

Or if you're not interested in this project and just want to play with a CH340G,
you can use an Arduino Nano (with a built in CH340G) running this sketch:

```cpp
void setup() {
    Serial.begin(9600);
    while (!Serial) {  }
}

void loop() {
    if (Serial.available() > 0) {
        switch(Serial.read()) {
            case '1':
                Serial.write("one\n");
                break;
            case '2':
                Serial.write("two\n");
                break;
            case '3':
                Serial.write("three\n");
                break;
            case '4':
                Serial.write("four\n");
                break;
        }
    }
}
```

## Other Chip Variants

I've stripped this prototype code down to the bare minimum to get the
**CH340G** working as I need it. If you're using other CH340 variants or this
CH341 that the Linux and BSD device drivers support
then we may need to put back in some of the code that I've stripped out.

## References

* [ch341-baudrate-calculation HOWTO](https://github.com/nospam2000/ch341-baudrate-calculation)
* [Yuxiang Zhang's libusb CH340 Data Transfer Demo](https://gist.github.com/z4yx/8d9ecad151dad351fbbb)
* FreeBSD driver
* NetBSD driver
* [Linux device driver for CH34x chips](https://github.com/lizard43/CH340G/blob/master/ch340g/ch34x.c)
* [felHR85 - Java Android driver for USB serial chips including the CH340 series](https://github.com/felHR85/UsbSerial/blob/7fff8b6d5ca19590dcb05c3f977970e8cce103b7/usbserial/src/main/java/com/felhr/usbserial/CH34xSerialDevice.java)
* [mik3y - Java Android driver for USB serial chips including the CH340 series](https://github.com/mik3y/usb-serial-for-android/blob/master/usbSerialForAndroid/src/main/java/com/hoho/android/usbserial/driver/Ch34xSerialDriver.java)
