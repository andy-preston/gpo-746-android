package andyp.gpo746

import kotlin.test.Test
import kotlin.test.assertTrue

// Coming to test my phone number validator with a real phone number; I realise
// that, no only is it hard to maintain, it also doesn't work. If I want a
// well maintained and open-source alternative my only choice is libphonenumber.
// And, as it has the worst documentation of any library ever, I'm going to have
// to test the hell out of it to work out if it does anything like what I want
// it to.

class PhoneNumberTest {
    @Test
    public fun start_with_a_failing_test() {
        assertTrue(false)
    }
}