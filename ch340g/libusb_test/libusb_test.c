#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <libusb.h>

static struct libusb_device_handle *device = NULL;
uint8_t byteBuffer[128];
uint16_t* intBuffer;
uint8_t version;
int status = 0;

#include "driver_functions.c"

int setupCH341G(void) {
    // My old code (that worked on libusb) had nothing about version in it anywhere
    if (!getVersion()) {
        return false;
    }
    // Prototype chip shows Version 0031
    fprintf(stderr, "Version %04x\n", version);
    if (!initialise()) {
        return false;
    }

    return status;
}

int claimInterface(void) {
    if (libusb_claim_interface(device, 0) < 0) {
        fprintf(stderr, "Failed to claim interface\n");
        return false;
    }
    status = setupCH341G();
    libusb_release_interface(device, 0);
    return status;
}

int usbDevice(void) {
    device = libusb_open_device_with_vid_pid(NULL, 0x1a86, 0x7523);
    if (device == NULL) {
        fprintf(stderr, "Failed to open device\n");
        return false;
    }
    status = claimInterface();
    libusb_close(device);
    return status;
}

int libUsb(void) {
    status = libusb_init(NULL);
    if (status < 0) {
        fprintf(stderr, "Failed to initialise libusb\n");
        return false;
    }
    status = usbDevice();
    libusb_exit(NULL);
    return status;
}

int main(int argc, char **argv) {
    intBuffer = (uint16_t*)byteBuffer;
    return libUsb() ? EXIT_SUCCESS : EXIT_FAILURE;
}
