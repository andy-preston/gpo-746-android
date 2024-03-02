package andyp.gpo746

/*
 * For the compile time calculations see:
 * ./src/buildSrc/src/main/kotlin/gpo746/Ch340gConstants.kt
 *
 * For want of ANY official documentation at all, this has been cargo-culted
 * from various sources.
 *
 * Android USB host serial driver library.
 * https://github.com/mik3y/usb-serial-for-android
 *
 * Usb serial controller for Android
 * https://github.com/felHR85/UsbSerial
 *
 * The FreeBSD CH341/340 Driver
 * https://github.com/freebsd/freebsd-src/blob/master/sys/dev/usb/serial/uchcom.c
 *
 * The NetBSD CH341/340 Driver
 * http://cvsweb.netbsd.org/bsdweb.cgi/~checkout~/src/sys/dev/usb/uchcom.c
 *
 * The Linux CH341 Driver
 * https://github.com/torvalds/linux/blob/master/drivers/usb/serial/ch341.c
 */

// This is the version used in the prototype hardware
// Some of the stuff I've seen in BSD drivers wants version >= 0030
// There are also differences writing handshake with version < 0020
const val CH340G_CHIP_VERSION: UShort = 0x0031u

enum class ReadRequest(val code: UByte) {
    VendorGetVersion(0x5Fu),
    VendorReadRegisters(0x95u)
}

enum class WriteRequest(val code: UByte) {
    VendorWriteRegisters(0x9Au),
    VendorSerialInit(0xA1u),
    VendorModemControl(0xA4u)
}

enum class ReadRegister(val address: UShort) {
    GCL(0x0706u)
}

// val baudModLow1 = "14", baudPaddingHigh2 = "0F"; // some drivers use 2C not 14

enum class WriteRegister(val address: UShort) {
    BaudDivisorPreScale(0x1312u),
    BaudMod(0x0F14u),
    LCR(0x2518u),
    GCL(0x0607u)
}

enum class ModemControlBit(val mask: UByte) {
    DTR(0x20u), // 1 shl 5
    RTS(0x40u) //  1 shl 6
}

enum class GclInputBit(val mask: UByte) {
    CTS(0x01u),
    DSR(0x02u),
    RI(0x04u),
    DCD(0x08u)
}

const val ZERO_BYTE: UByte = 0u

@Suppress("MagicNumber")
@OptIn(kotlin.ExperimentalUnsignedTypes::class)
abstract class UsbResultBuffer {
    protected fun shortFromBuffer(buffer: ByteArray?): UShort {
        // As this buffer may possibly have come from C code, there is very
        // little we can do about the possibility of a null pointer. So, here,
        // I'm trying to return a "sensible" answer in the case of a null.
        // It might be better to raise an exception but I think this is "best".
        if (buffer == null) {
            return 0u
        } else {
            val lowByte = buffer[1].toUInt() and 0xFFu
            val highByte = buffer[0].toUInt() and 0xFFu
            return ((lowByte shl 8) or highByte).toUShort()
        }
    }
}

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class Ch340g(usbSystem: UsbSystemInterface) : UsbResultBuffer() {

    private val usb = usbSystem

    private fun writeRequest(
        request: WriteRequest,
        valueOrRegister: UShort,
        zeroOrValue: UShort
    ) {
        usb.write(request.code, valueOrRegister, zeroOrValue)
    }

    private fun write(request: WriteRequest, value: UShort) {
        writeRequest(request, value, 0u)
    }

    private fun writeRegisters(register: WriteRegister, value: UShort) {
        writeRequest(WriteRequest.VendorWriteRegisters, register.address, value)
    }

    private fun readRequest(
        request: ReadRequest,
        zeroOrRegister: UShort
    ): UShort {
        return shortFromBuffer(usb.read(request.code, zeroOrRegister))
    }

    private fun read(request: ReadRequest): UShort {
        return readRequest(request, 0u)
    }

    private fun readRegisters(register: ReadRegister): UShort {
        return readRequest(ReadRequest.VendorReadRegisters, register.address)
    }

    public fun readSerial(): String {
        return usb.bulkRead().decodeToString().substringBefore('\u0000')
    }

    public fun start() {
        val version: UShort = read(ReadRequest.VendorGetVersion)
        val expectedVersion = CH340G_CHIP_VERSION.toUShort()
        if (version != expectedVersion) {
            throw Ch340Exception(
                "version should be $expectedVersion, but it's $version"
            )
        }
        write(WriteRequest.VendorSerialInit, 0u)
        writeRegisters(WriteRegister.BaudDivisorPreScale, CH340G_DIVISOR_PRESCALER)
        writeRegisters(WriteRegister.BaudMod, CH340G_BAUD_MOD)
        writeRegisters(WriteRegister.LCR, CH340G_DEFAULT_LCR)
    }

    public fun writeHandshake(outputRTS: Boolean, outputDTR: Boolean) {
        // This should be a logical OR but, during debugging, I made it an
        // add instead and both are more or less the same.
        val modemControl: UShort = (
            (if (outputRTS) ModemControlBit.RTS.mask else ZERO_BYTE) or
            (if (outputDTR) ModemControlBit.DTR.mask else ZERO_BYTE)
        ).toUShort()
        // For a chip version > 0x20, Write to Modem Control
        // For a lower version, Write to GCL
        // But on my version 0x31 chip, both seem to work
        write(WriteRequest.VendorModemControl, modemControl)
    }

    public fun readHandshake(): Boolean {
        val modemControl: UShort = readRegisters(ReadRegister.GCL)
        val mask = GclInputBit.RI.mask.toUShort()
        return (modemControl and mask) == mask
    }
}
