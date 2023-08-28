#include <stdint.h>

struct libusb_device_handle;
typedef struct libusb_device_handle libusb_device_handle;

struct libusb_context;
typedef struct libusb_context libusb_context;

enum libusb_request_type {
	LIBUSB_REQUEST_TYPE_STANDARD = (0x00 << 5),
	LIBUSB_REQUEST_TYPE_CLASS = (0x01 << 5),
	LIBUSB_REQUEST_TYPE_VENDOR = (0x02 << 5),
	LIBUSB_REQUEST_TYPE_RESERVED = (0x03 << 5)
};

enum libusb_endpoint_direction {
	LIBUSB_ENDPOINT_IN = 0x80,
	LIBUSB_ENDPOINT_OUT = 0x00
};

int libusb_init(
    void * null_context
);

void libusb_close(
    libusb_device_handle *dev_handle
);

void libusb_exit(
    libusb_context *ctx
);

libusb_device_handle * libusb_open_device_with_vid_pid(
	libusb_context *ctx,
    uint16_t vendor_id,
    uint16_t product_id
);

int libusb_claim_interface(
    libusb_device_handle *dev_handle,
	int interface_number
);

int libusb_release_interface(
    libusb_device_handle *dev_handle,
	int interface_number
);

int libusb_control_transfer(
    libusb_device_handle *dev_handle,
	uint8_t request_type,
    uint8_t bRequest,
    uint16_t wValue,
    uint16_t wIndex,
	unsigned char *data,
    uint16_t wLength,
    unsigned int timeout
);

int libusb_bulk_transfer(
    libusb_device_handle *dev_handle,
	unsigned char endpoint,
    unsigned char *data,
    int length,
	int *actual_length,
    unsigned int timeout
);
