package gpo_746

import kotlin.test.Test
import kotlin.test.assertEquals

class UsbInterfaceTest {

    fun stringify(byteOrFailure: ByteOrFailure): String {
        return when (byteOrFailure) {
            is ByteOrFailure.Success -> "success ${byteOrFailure.value}"
            is ByteOrFailure.Failure -> "failure ${byteOrFailure.message}"
        }
    }

    @Test
    fun an__x_or_failure__result_can_have_a_success_or_failure_value() {
        assertEquals(
            "success 5",
            stringify(ByteOrFailure.Success(5u))
        )
        assertEquals(
            "failure I fell over!",
            stringify(ByteOrFailure.Failure("I fell over!"))
        )
    }

}