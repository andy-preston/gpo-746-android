# CH340G Driver

In this directory you'll find stuff related to driving CH340G USB/Serial chips.

* [Driver spec](https://github.com/andy-preston/gpo-746-android/tree/android_ch340g_driver/ch340g/driver-spec)
An over engineered TypeScript program to generate the driver functions in C and Kotlin from a generalised spec.
See the main [Makefile](https://github.com/andy-preston/gpo-746-android/blob/android_ch340g_driver/Makefile) for details.

* [LibUSB test](https://github.com/andy-preston/gpo-746-android/tree/android_ch340g_driver/ch340g/libusb_test)
Basic C code to test the driver on a Linux system (Raspberry Pi) using `libusb` - assumes kernel driver has been blacklisted.
