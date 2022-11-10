#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <libusb.h>

#define USB_TIMEOUT 1000

// Request Codes
#define VENDOR_VERSION 0x5F
#define VENDOR_READ 0x95
#define VENDOR_WRITE 0x9A
#define VENDOR_SERIAL_INIT 0xA1
#define VENDOR_MODEM_OUT 0xA4

// Registers
#define BAUDRATE_1 0x1312
#define LCR 0x2518
#define GCL 0x0706
#define BAUDRATE_2 0x0F2C

// LCR Bits
#define ENABLE_RX 0x80
#define ENABLE_TX 0x40
#define MARK_SPACE 0x20
#define PAR_EVEN 0x10
#define ENABLE_PAR 0x08
#define STOP_BITS_2 0x04
#define CS8 0x03
#define CS7 0x02
#define CS6 0x01
#define CS5 0x00


static struct libusb_device_handle *device = NULL;

char sendChar = '1';
unsigned char buffer[128];
int status = 0;

int setup() {
    device = libusb_open_device_with_vid_pid(NULL, 0x1a86, 0x7523);
    if (device == NULL) {
        fprintf(stderr, "Failed to open device\n");
        return -1;
    }
    if (libusb_claim_interface(device, 0) < 0) {
        fprintf(stderr, "Failed to claim interface\n");
        return -1;
    }
    return 0;
}

int controlOut(int requestCode, int value, int index) {
    return libusb_control_transfer(
        device,
        LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_OUT,
        requestCode,
        value,
        index,
        NULL,
        0,
        USB_TIMEOUT
    );
}

int controlIn(int requestCode, int value) {
    return libusb_control_transfer(
        device,
        LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_IN,
        requestCode,
        value,
        0,
        buffer,
        180,
        USB_TIMEOUT
    );
}


void setHandshake(uint8_t dtr, uint8_t rts) {
    status = controlOut(VENDOR_MODEM_OUT, ~((dtr ? 1 << 5 : 0) | (rts ? 1 << 6 : 0)), 0);
    if (status < 0) {
        fprintf(stderr, "Failed to set handshake\n");
        return;
    }
}

void getHandshake(uint8_t* cts, uint8_t* dsr, uint8_t* ri, uint8_t* dcd) {
    // We get 2 bytes from the status register
    // but I'm still unsure of what the second byte is for
    uint8_t bytes = controlIn(VENDOR_READ, GCL);
    if (bytes < 2) {
        status = -1;
        fprintf(stderr, "Failed to get handshake\n");
        return;
    }
    // Other drivers seem to use the Low nybble,
    // but I'm getting these in the high nybble!
    *cts = (buffer[0] & 0x10) == 0;
    *dsr = (buffer[0] & 0x20) == 0;
    *ri = (buffer[0] & 0x40) == 0;
    *dcd = (buffer[0] & 0x80) == 0;
}

void initialise() {
    status = controlOut(VENDOR_SERIAL_INIT, 0, 0);
    if (status < 0) {
        fprintf(stderr, "Failed VENDOR_SERIAL_INIT 0,0\n");
        return;
    }
    status = controlOut(VENDOR_WRITE, LCR, ENABLE_TX | ENABLE_RX | CS8);
    if (status < 0) {
        fprintf(stderr, "Failed to set LCR\n");
        return;
    }
    /* I have no idea what 0x501f is or why we'd want to write 0xd90a to it!
       But things seem to work fine without it anyway
    status = controlOut(VENDOR_SERIAL_INIT, 0x501f, 0xd90a);
    if (status < 0) {
        fprintf(stderr, "Failed 0x501f 0xd90a\n");
        return;
    }
    */

    #define BAUD_RATE 9600

    int baud1 = 0, baud2 = 0;
    switch (BAUD_RATE) {
        case 2400: baud1 = 0xd901; baud2 = 0x0038; break;
        case 4800: baud1 = 0x6402; baud2 = 0x001f; break;
        case 9600: baud1 = 0xb202; baud2 = 0x0013; break;
        case 19200: baud1 = 0xd902; baud2 = 0x000d; break;
        case 38400: baud1 = 0x6403; baud2 = 0x000a; break;
        case 115200: baud1 = 0xcc03; baud2 = 0x0008; break;
    };
    status = controlOut(VENDOR_WRITE, BAUDRATE_1, baud1);
    if (status < 0) {
        fprintf(stderr, "Failed to set BAUDRATE_1\n");
        return;
    }
    status = controlOut(VENDOR_WRITE, BAUDRATE_2, baud2);
    if (status < 0) {
        fprintf(stderr, "Failed to set BAUDRATE_1\n");
        return;
    }
    setHandshake(0, 0);
}

void send() {
    int transferred;
    buffer[0] = sendChar;
    buffer[1] = 0;
    status = libusb_bulk_transfer(
        device,
        // What is the 2 for?
        0x2 | LIBUSB_ENDPOINT_OUT,
        buffer,
        1,
        &transferred,
        200
    );
    if (status < 0) {
        fprintf(stderr, "Failed to send\n");
    }
}

void receive() {
    int transferred;
    status = libusb_bulk_transfer(
        device,
        // What is the 2 for?
        0x02 | LIBUSB_ENDPOINT_IN,
        buffer,
        sizeof(buffer),
        &transferred,
        0
    );
    if (status < 0) {
        fprintf(stderr, "Failed to receive\n");
        return;
    }
    for (int i = 0; i < transferred; ++i) {
        putchar(buffer[i]);
    }
    fflush(stdout);
}

int main(int argc, char **argv) {
    uint8_t rts;
    uint8_t ri;
    uint8_t iDontCare;
    if (libusb_init(NULL) < 0) {
        fprintf(stderr, "Failed to initialise libusb\n");
    } else {
        if (setup() == 0) {
            initialise();
            rts = 0;
            while (1) {
                if (status != 0) {
                    fprintf(stderr, "unknown error - non zero status %x\n", status);
                    break;
                }
                if (status == 0) {
                    send();
                }
                if (status == 0) {
                    getHandshake(&iDontCare, &iDontCare, &ri, &iDontCare);
                    fputs (ri ? "RI " : "   ", stdout);
                }
                if (status == 0) {
                    receive();
                }
                if (++sendChar > '4') {
                    sendChar = '1';
                    rts = !rts;
                    if (status == 0) setHandshake(0, rts);
                }
            }
            libusb_release_interface(device, 0);
        }
        libusb_close(device);
        libusb_exit(NULL);
    }
    return 0;
}
