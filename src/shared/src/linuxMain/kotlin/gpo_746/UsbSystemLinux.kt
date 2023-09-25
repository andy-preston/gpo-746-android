package gpo_746

import kotlinx.cinterop.*
import libusb.*

class UsbSystemLinux() : UsbSystemInterface {

    private var interfaceClaimed: Boolean = false

    private var libInitialised: Boolean = false

    private var buffer = UByteArray(16) // should be macro expansion for size

    // private val buffer CValuesRef<UByteVar>?

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    private var device: CPointer<libusb_device_handle>? = null

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override public fun open() {
        var status: Int

        status = libusb_init(null)
        libInitialised = status == 0
        if (!libInitialised) {
            throw Exception("Failed to initialise libusb: ${status}")
        }
        device = libusb_open_device_with_vid_pid(null, 0x1a86u, 0x7523u)
        if (device == null) {
            throw Exception("Failed to open USB device")
        }
        status = libusb_claim_interface(device, 0)
        interfaceClaimed = status == 0
        if (!interfaceClaimed) {
            throw Exception("Failed to claim interface: ${status}")
        }

    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override public fun close() {
        if (interfaceClaimed) {
            libusb_release_interface(device, 0)
            interfaceClaimed = false
        }
        if (device != null) {
            libusb_close(device)
            device = null
        }
        if (libInitialised) {
            libusb_exit(null)
            libInitialised = false
        }
    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override public fun bulkRead(): Array<UByte> {
        var status: Int = 0
        val transferred: Int = memScoped {
            var transferred_c = alloc<IntVar>()
            status = libusb_bulk_transfer(
                device,
                0x82u, // TODO: should be macro expansion @bulkInputEndpoint@,
                buffer.refTo(0),
                16 - 1, // TODO: buffer size should be macro expansion
                transferred_c.ptr,
                0u
            )
            transferred_c.value
        }
        if (status != 0) {
            throw Exception("Bulk read failed ${status}")
        }
        buffer[transferred] = 0u
        return buffer.toTypedArray()
    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override public fun read(
        requestCode: UByte,
        addressOrPadding: UShort
    ): Array<UByte> {
        val status: Int = libusb_control_transfer(
            device,
            (LIBUSB_REQUEST_TYPE_VENDOR or LIBUSB_ENDPOINT_IN).toUByte(),
            requestCode,
            addressOrPadding,
            0u,
            buffer.refTo(0),
            2u,
            1000u // TODO: should be macro expansion to @usbTimeout@
        )
        if (status != 0) {
            throw Exception("Control transfer read failed ${status}")
        }
        return buffer.toTypedArray()
    }

    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    override public fun write(
        requestCode: UByte,
        addressOrValue: UShort,
        valueOrPadding: UShort
    ) {
        val status: Int = libusb_control_transfer(
            device,
            (LIBUSB_REQUEST_TYPE_VENDOR or LIBUSB_ENDPOINT_OUT).toUByte(),
            requestCode,
            addressOrValue,
            valueOrPadding,
            null,
            0u,
            1000u // TODO: should be macro expansion to @usbTimeout@
        )
        if (status != 0) {
            throw Exception("Control transfer write failed ${status}")
        }
    }
}
