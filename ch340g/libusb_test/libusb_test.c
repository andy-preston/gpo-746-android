#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <unistd.h>
#include <string.h>
#include <libusb.h>

static struct libusb_device_handle *device = NULL;
int status = 0;

union Buffer {
    uint8_t bytes[128];
    uint16_t words[64];
} buffer;

/* Yes, I know this is a damn wierd was to link functions in C
 * But I've always held that this preprocessor gubbins was a
 * damn wierd way of going about things in the first place
 */
#include "driver_functions.c"

int libUsb(void);
int usbDevice(void);
int claimInterface(void);
int setup(void);
int testRtsOutput(void);
char *testName;

int main(int argc, char **argv) {
    testName = argc == 2 ? argv[1] : "none";
    return libUsb() ? EXIT_SUCCESS : EXIT_FAILURE;
}

int testRtsOutput(void) {
    fprintf(stdout, "testing RTS Output\n");
    while (true) {
        if (!setHandshake()) {
            return false;
        }
        sleep(1);
        handshakeOutputRTS = true;
        if (!setHandshake()) {
            return false;
        };
        sleep(1);
        handshakeOutputRTS = false;
    }
}

int tests() {
    if (strcmp(testName, "rts-output") == 0) {
        return testRtsOutput();
    }
    return false;
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

int claimInterface(void) {
    if (libusb_claim_interface(device, 0) < 0) {
        fprintf(stderr, "Failed to claim interface\n");
        return false;
    }
    status = setup() ? 0 : -1;
    libusb_release_interface(device, 0);
    return status;
}

int setup(void) {
    if (!initialise()) {
        return false;
    }
    if (!setHandshake()) {
        return false;
    }
    return tests();
}

