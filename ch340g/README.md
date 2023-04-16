# CH340G Driver

In this directory you'll find stuff relatedd to driving CH340G USB/Serial chips.

* [Driver spec](https://github.com/andy-preston/gpo-746-android/tree/android_ch340g_driver/ch340g/driver-spec)
an over engineered TypeScript program to generate the driver functions in C and Kotlin from a generalised spec.

* [LibUSB test](https://github.com/andy-preston/gpo-746-android/tree/android_ch340g_driver/ch340g/libusb_test)
basic C code to test the driver on a Linux system (Raspberry Pi) using `libusb` - assumes kernel driver has been blacklisted
