import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Ch340gBaudRateTest {

    @Test
    fun invalid_baud_rate_throws_an_exception() {
        val exception = assertFailsWith<Exception>(
            block = {
                Ch340gBaudRate(1024).checkedRate()
            }
        )
        assertEquals(
            "Invalid baud rate 1024 not in [2400, 4800, 9600, 19200, 38400, 115200]",
            exception.message
        )
    }
}
