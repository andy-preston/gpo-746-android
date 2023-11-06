package gpo_746

import kotlinx.cinterop.*
import libusb.*

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class UsbSystemMock() : UsbSystemInterface {
    private var libInitialised: Boolean = false
    private var interfaceClaimed: Boolean = false

    private var timeoutMilliseconds: UInt = 1000u
    private var handle: CPointer<libusb_device_handle>? = null
    private var device: CPointer<libusb_device>? = null
    private var bulkReadEndpoint: UByte = 0u
    private var packetSize: Int = 0
    private var buffer: UByteArray? = null

    private fun assertTrue(condition: Boolean, operationHint: String) {
        if (!condition) {
            throw Exception("**Failed** ${operationHint}")
        }
    }

    private fun assertSuccess(
        statusCode: Int,
        operationHint: String,
        timeoutIsError: Boolean = true
    ) {
        val statusMessages = mapOf(
            LIBUSB_SUCCESS to "",
            LIBUSB_ERROR_IO to "IO error",
            LIBUSB_ERROR_INVALID_PARAM to "Invalid parameter",
            LIBUSB_ERROR_ACCESS to "Access error",
            LIBUSB_ERROR_NO_DEVICE to "No device",
            LIBUSB_ERROR_NOT_FOUND to "Not found",
            LIBUSB_ERROR_BUSY to "Busy",
            LIBUSB_ERROR_TIMEOUT to if (timeoutIsError) "Timeout" else "",
            LIBUSB_ERROR_OVERFLOW to "Overflow",
            LIBUSB_ERROR_PIPE to "Pipe!",
            LIBUSB_ERROR_INTERRUPTED to "Interrupted",
            LIBUSB_ERROR_NO_MEM to "No memory",
            LIBUSB_ERROR_NOT_SUPPORTED to "Not supported",
            LIBUSB_ERROR_OTHER to "Unknown error (${statusCode})",
        )
        val statusMessage = statusMessages[
            if (statusMessages.keys.contains(statusCode))
                statusCode else LIBUSB_ERROR_OTHER
        ]
        assertTrue(
            statusMessage == "",
            "${statusMessage} during ${operationHint}"
        )
    }

    private fun openDevice(vid: UShort, pid: UShort) {
        handle = libusb_open_device_with_vid_pid(null, vid, pid)
        assertTrue(handle != null, "libusb_open_device_with_vid_pid")
        device = libusb_get_device(handle)
        assertTrue(device != null, "libusb_get_device")
    }

    private fun claimInterface() {
        if (libusb_kernel_driver_active(handle, 0) == 1) {
            assertSuccess(
                libusb_detach_kernel_driver(handle, 0),
                "libusb_detach_kernel_driver"
            )
        }
        assertSuccess(
            libusb_claim_interface(handle, 0),
            "libusb_claim_interface"
        )
        interfaceClaimed = true
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

    ////////////////////////////////////////////////////////////////////////////

    override public fun start(vid: UShort, pid: UShort, timeout: Int) {
        timeoutMilliseconds = timeout.toUInt()

        assertSuccess(libusb_init(null), "libusb_init")
        libInitialised =  true
        openDevice(vid, pid)
        claimInterface()
        memScoped {
            val config = alloc<CPointerVar<libusb_config_descriptor>>()
            assertSuccess(
                libusb_get_active_config_descriptor(device, config.ptr),
                "libusb_get_active_config_descriptor"
            )
            val iFace = config.pointed!!.`interface`!![0].altsetting!![0]
            bulkReadEndpoint = findReadEndpoint(
                iFace.endpoint!!,
                iFace.bNumEndpoints.toInt()
            )
        }
        assertTrue(bulkReadEndpoint.toUInt() != 0u, "Find bulk read endpoint")
        packetSize = libusb_get_max_packet_size(device, bulkReadEndpoint)
        buffer = UByteArray(maxOf(packetSize + 1, 2))
    }

    override public fun finish() {
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

    override public fun bulkRead(): ByteArray {
        var statusCode: Int = 0
        val transferred: Int = memScoped {
            val transferred_c = alloc<IntVar>()
            statusCode = libusb_bulk_transfer(
                handle,
                bulkReadEndpoint,
                buffer!!.refTo(0),
                packetSize,
                transferred_c.ptr,
                timeoutMilliseconds
            )
            transferred_c.value
        }
        assertSuccess(statusCode, "Bulk read", false)
        buffer!![transferred] = 0u
        return buffer!!.toByteArray()
    }

    override public fun read(
        requestCode: UByte,
        addressOrPadding: UShort
    ): ByteArray {
        val transferred: Int = libusb_control_transfer(
            handle,
            (LIBUSB_REQUEST_TYPE_VENDOR or LIBUSB_ENDPOINT_IN).toUByte(),
            requestCode,
            addressOrPadding,
            0u,
            buffer!!.refTo(0),
            packetSize.toUShort(),
            timeoutMilliseconds
        )
        assertTrue(
            transferred == 2,
            "Control transfer read ${transferred} bytes, 2 expected"
        )
        return buffer!!.toByteArray()
    }

    override public fun write(
        requestCode: UByte,
        addressOrValue: UShort,
        valueOrPadding: UShort
    ) {
        val statusCode: Int = libusb_control_transfer(
            handle,
            (LIBUSB_REQUEST_TYPE_VENDOR or LIBUSB_ENDPOINT_OUT).toUByte(),
            requestCode,
            addressOrValue,
            valueOrPadding,
            null,
            0u,
            timeoutMilliseconds
        )
        assertSuccess(statusCode, "Control transfer write failed ${statusCode}")
    }
}
