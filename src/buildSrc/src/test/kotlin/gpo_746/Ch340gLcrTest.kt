import kotlin.test.Test
import kotlin.test.assertEquals

class Ch340gLcrTest {

    @Test
    fun lets_just_see_if_the_lcr_stuff_works() {
        val defaultLcr = Ch340gLcr().defaultLcr().toString(16)
        assertEquals("83", defaultLcr)
    }

}
