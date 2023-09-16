package gpo_746

sealed class ByteOrFailure {
    class Success(val value: UByte): ByteOrFailure()
    class Failure(val message: String): ByteOrFailure()
}

sealed class ByteArrayOrFailure {
    class Success(val value: Array<UByte>): ByteArrayOrFailure()
    class Failure(val message: String): ByteArrayOrFailure()
}

sealed class NullOrFailure {
    class Success(val value: UByte?): NullOrFailure()
    class Failure(val message: String): NullOrFailure()
}

interface UsbSystemInterface {
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
