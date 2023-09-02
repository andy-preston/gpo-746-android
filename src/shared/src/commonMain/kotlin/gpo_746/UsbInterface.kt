package gpo_746

import gpo_746.Ch340gRegisters

sealed class ByteOrFailure {
    inline data class Success(val value: Byte): ByteOrFailure()
    inline data class Error(val message: String): ByteOrFailure()
}

sealed class ByteArrayOrFailure {
    inline data class Success(val value: Array<Byte>): ByteArrayOrFailure()
    inline data class Error(val message: String): ByteArrayOrFailure()
}

sealed class NullOrFailure {
    inline data class Success(val value: Byte?): NullOrFailure()
    inline data class Error(val message: String): NullOrFailure()
}

interface UsbInterface {
    fun open()

    fun close()

    fun bulkRead(): ByteArrayOrFailure

    fun read(
        title: String,
        request: ReadRequest,
        register: ReadRegister
    ): ByteOrFailure

    fun write(
        title: String,
        request: WriteRequest,
        register: WriteRegister,
        value: Short
    ): NullOrFailure
}
