package andyp.gpo746

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// https://en.wikipedia.org/wiki/Telephone_numbers_in_the_United_Kingdom

class PhoneNumberValidatorTest {

    private val validator = PhoneNumberValidator()
    private val testLengthLimit = 30

    private fun assertHasLength(number: String, correctLength: Int) {
        var testNumber = number
        while (testNumber.length < testLengthLimit) {
            when (validator.result(testNumber)) {
                ValidatorResult.Incomplete -> assertTrue(testNumber.length < correctLength)
                ValidatorResult.Invalid -> assertTrue(testNumber.length > correctLength)
                ValidatorResult.Good -> assertTrue(testNumber.length == correctLength)
            }
            testNumber = "${testNumber}7"
        }
    }

    private fun assertAnyLengthInvalid(number: String) {
        var testNumber = number
        while (testNumber.length < testLengthLimit) {
            assertEquals(ValidatorResult.Invalid, validator.result(testNumber))
            testNumber = "${testNumber}7"
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // // // //

    @Test
    fun zero_length_numbers_are_considered_incomplete() {
        assertEquals(ValidatorResult.Incomplete, validator.result(""))
    }

    // // // // // // // // // // // // // // // // // // // // // // // // //

    @Test
    fun all_numbers_that_do_not_begin_with_0_are_invalid() {
        /* this will no longer be correct when we add 3 digit numbers
         * like 100, 999, etc.
         */
        (1..9).iterator().forEach {
            assertAnyLengthInvalid("$it")
        }
    }

    @Test
    fun all_numbers_that_start_with_04_or_06_are_invalid() {
        assertAnyLengthInvalid("04")
        assertAnyLengthInvalid("06")
    }

    // // // // // // // // // // // // // // // // // // // // // // // // //

    @Test
    fun numbers_that_start_with_09_must_have_11_digits() {
        assertHasLength("09", 11)
    }

    @Test
    fun numbers_that_start_with_0800_must_have_10_digits() {
        assertHasLength("0800", 10)
    }

    @Test
    fun numbers_that_start_with_08_but_not_0800_must_have_11_digits() {
        assertHasLength("08", 11)
    }

    @Test
    fun numbers_that_start_with_019756_must_have_12_digits() {
        assertHasLength("019756", 12)
    }

    @Test
    fun passes_019755_numbers_with_12_digits_only() {
        assertHasLength("019755", 12)
    }

    @Test
    fun passes_019467_numbers_with_12_digits_only() {
        assertHasLength("019467", 12)
    }

    @Test
    fun passes_017683_numbers_with_12_digits_only() {
        assertHasLength("017683", 12)
    }

    @Test
    fun passes_017684_numbers_with_12_digits_only() {
        assertHasLength("017684", 12)
    }

    @Test
    fun passes_017687_numbers_with_12_digits_only() {
        assertHasLength("017687", 12)
    }

    @Test
    fun passes_013397_numbers_with_12_digits_only() {
        assertHasLength("013397", 12)
    }

    @Test
    fun passes_013398_numbers_with_12_digits_only() {
        assertHasLength("013398", 12)
    }

    @Test
    fun passes_013873_numbers_with_12_digits_only() {
        assertHasLength("013873", 12)
    }

    @Test
    fun passes_015242_numbers_with_12_digits_only() {
        assertHasLength("015242", 12)
    }

    @Test
    fun passes_015394_numbers_with_12_digits_only() {
        assertHasLength("015394", 12)
    }

    @Test
    fun passes_015395_numbers_with_12_digits_only() {
        assertHasLength("015395", 12)
    }

    @Test
    fun passes_015396_numbers_with_12_digits_only() {
        assertHasLength("015396", 12)
    }

    @Test
    fun passes_016973_numbers_with_12_digits_only() {
        assertHasLength("016973", 12)
    }

    @Test
    fun passes_016974_numbers_with_12_digits_only() {
        assertHasLength("016974", 12)
    }

    @Test
    fun passes_07_numbers_with_12_digits_only() {
        assertHasLength("07", 12)
    }

    @Test
    fun passes_05_numbers_with_12_digits_only() {
        assertHasLength("05", 12)
    }

    @Test
    fun passes_03_numbers_with_12_digits_only() {
        assertHasLength("03", 12)
    }

    @Test
    fun passes_02_numbers_with_12_digits_only() {
        assertHasLength("02", 12)
    }

    // // // // // // // // // // // // // // // // // // // // // // // // //

    @Test
    fun passes_brampton_cumbria_01697_72_numbers_with_10_digits_only() {
        assertHasLength("0169772", 10)
    }

    @Test
    fun passes_brampton_cumbria_01697_73_numbers_with_10_digits_only() {
        assertHasLength("0169773", 10)
    }

    // // // // // // // // // // // // // // // // // // // // // // // // //

    @Test
    fun passes_brampton_cumbria_01697_2_numbers_with_11_digits_only() {
        assertHasLength("016972", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_5_numbers_with_11_digits_only() {
        assertHasLength("016975", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_6_numbers_with_11_digits_only() {
        assertHasLength("016976", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_8_numbers_with_11_digits_only() {
        assertHasLength("016978", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_9_numbers_with_11_digits_only() {
        assertHasLength("016979", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_74_numbers_with_11_digits_only() {
        assertHasLength("0169774", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_75_numbers_with_11_digits_only() {
        assertHasLength("0169775", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_76_numbers_with_11_digits_only() {
        assertHasLength("0169776", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_77_numbers_with_11_digits_only() {
        assertHasLength("0169777", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_78_numbers_with_11_digits_only() {
        assertHasLength("0169778", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_790_numbers_with_11_digits_only() {
        assertHasLength("01697790", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_791_numbers_with_11_digits_only() {
        assertHasLength("01697791", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_792_numbers_with_11_digits_only() {
        assertHasLength("01697792", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_793_numbers_with_11_digits_only() {
        assertHasLength("01697793", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_794_numbers_with_11_digits_only() {
        assertHasLength("01697794", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_795_numbers_with_11_digits_only() {
        assertHasLength("01697795", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_796_numbers_with_11_digits_only() {
        assertHasLength("01697796", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_797_numbers_with_11_digits_only() {
        assertHasLength("01697797", 11)
    }

    @Test
    fun passes_brampton_cumbria_01697_798_numbers_with_11_digits_only() {
        assertHasLength("01697798", 11)
    }

    // // // // // // // // // // // // // // // // // // // // // // // // //

    // 01#1 ### ####   11
    // 011# ### ####   11
    // 01### ######    11
    // 01### #####     10
}
