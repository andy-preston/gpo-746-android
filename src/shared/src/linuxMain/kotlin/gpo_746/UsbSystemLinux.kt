package gpo_746

import kotlinx.cinterop.*
import libusb.*

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class UsbSystemLinux() : UsbSystemInterface {
    private var libInitialised: Boolean = false
    private var interfaceClaimed: Boolean = false

    private var usbTimeout: UInt = 1000u
    private var handle: CPointer<libusb_device_handle>? = null
    private var device: CPointer<libusb_device>? = null
    private var bulkReadEndpoint: UByte = 0u
    private var buffer: UByteArray? = null

    private fun assertTrue(condition: Boolean, operationHint: String) {
        if (!condition) {
            throw Exception("**Failed** ${operationHint}")
        }
    }

    private fun assertZeroStatus(status: Int, operationHint: String): Boolean {
        val condition = status == 0
        assertTrue(condition, "${status} - ${operationHint}")
        return condition
    }

    private fun openDevice(vid: UShort, pid: UShort) {
        handle = libusb_open_device_with_vid_pid(null, vid, pid)
        assertTrue(handle != null, "libusb_open_device_with_vid_pid")
        device = libusb_get_device(handle)
        assertTrue(device != null, "libusb_get_device")
    }

    private fun claimInterface() {
        if (libusb_kernel_driver_active(handle, 0) == 1) {
            println("Linux driver is active... detaching")
            assertZeroStatus(
                libusb_detach_kernel_driver(handle, 0),
                "libusb_detach_kernel_driver"
            )
        }
        interfaceClaimed = assertZeroStatus(
            libusb_claim_interface(handle, 0),
            "libusb_claim_interface"
        )
    }

    private inline fun isInput(endpoint: libusb_endpoint_descriptor): Boolean {
        return endpoint.bEndpointAddress.and(
            LIBUSB_ENDPOINT_DIR_MASK.toUByte()
        ) == LIBUSB_ENDPOINT_IN.toUByte()
    }

    private inline fun isBulk(endpoint: libusb_endpoint_descriptor): Boolean {
        return endpoint.bmAttributes.and(
            LIBUSB_TRANSFER_TYPE_MASK.toUByte()
        ) == LIBUSB_TRANSFER_TYPE_BULK.toUByte()
    }

    private inline fun interfaceDescriptor(
        config: CPointerVar<libusb_config_descriptor>
    ): libusb_interface_descriptor {
        return config.pointed!!.`interface`!!.pointed.altsetting!!.pointed
    }

    private inline fun findReadEndpoint(
        endpoints: CPointer<libusb_endpoint_descriptor>,
        numEndpoints: Int
    ) : UByte {
        for (index in 0..numEndpoints - 1) {
            val endpoint = endpoints[index]
            if (isInput(endpoint) and isBulk(endpoint)) {
                return endpoint.bEndpointAddress
            }
        }
        return 0u
    }

    override public fun open(vid: UShort, pid: UShort, timeout: Int) {
        usbTimeout = timeout.toUInt()

        libInitialised = assertZeroStatus(libusb_init(null), "libusb_init")
        openDevice(vid, pid)
        claimInterface()
        memScoped {
            val config = alloc<CPointerVar<libusb_config_descriptor>>()
            assertZeroStatus(
                libusb_get_active_config_descriptor(device, config.ptr),
                "libusb_get_active_config_descriptor"
            )
            val iFace = interfaceDescriptor(config)
            bulkReadEndpoint = findReadEndpoint(
                iFace.endpoint!!,
                iFace.bNumEndpoints.toInt()
            )
        }
        assertTrue(bulkReadEndpoint.toUInt() != 0u, "Find bulk read endpoint")
        val packetSize = libusb_get_max_packet_size(device, bulkReadEndpoint)
        buffer = UByteArray(packetSize)
    }

    override public fun close() {
        if (interfaceClaimed) {
            libusb_release_interface(handle, 0)
            interfaceClaimed = false
        }
        if (handle != null) {
            libusb_close(handle)
            handle = null
        }
        if (libInitialised) {
            libusb_exit(null)
            libInitialised = false
        }
    }

    override public fun bulkRead(): Array<UByte> {
        var status: Int = 0
        val transferred: Int = memScoped {
            var transferred_c = alloc<IntVar>()
            status = libusb_bulk_transfer(
                handle,
                bulkReadEndpoint,
                buffer!!.refTo(0),
                16 - 1, // TODO: buffer size should be macro expansion
                transferred_c.ptr,
                usbTimeout
            )
            transferred_c.value
        }
        if (status != 0) {
            throw Exception("Bulk read failed ${status}")
        }
        buffer!![transferred] = 0u
        return buffer!!.toTypedArray()
    }

    override public fun read(
        requestCode: UByte,
        addressOrPadding: UShort
    ): Array<UByte> {
        val status: Int = libusb_control_transfer(
            handle,
            (LIBUSB_REQUEST_TYPE_VENDOR or LIBUSB_ENDPOINT_IN).toUByte(),
            requestCode,
            addressOrPadding,
            0u,
            buffer!!.refTo(0),
            2u,
            usbTimeout
        )
        if (status != 0) {
            throw Exception("Control transfer read failed ${status}")
        }
        return buffer!!.toTypedArray()
    }

    override public fun write(
        requestCode: UByte,
        addressOrValue: UShort,
        valueOrPadding: UShort
    ) {
        val status: Int = libusb_control_transfer(
            handle,
            (LIBUSB_REQUEST_TYPE_VENDOR or LIBUSB_ENDPOINT_OUT).toUByte(),
            requestCode,
            addressOrValue,
            valueOrPadding,
            null,
            0u,
            usbTimeout
        )
        if (status != 0) {
            throw Exception("Control transfer write failed ${status}")
        }
    }
}
