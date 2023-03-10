package com.gitlab.edgeeffect.gpo746

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame


class ValidatePhoneNumberTest {

    @Test
    fun fails_zero_length_numbers() {
        assertFalse(validatePhoneNumber(""))
    }

    @Test
    fun fails_all_numbers_that_dont_begin_with_0() {
        (1..9).iterator().forEach {
            assertFalse(validatePhoneNumber("${it}7654321"))
        }
    }

    @Test
    fun fails_all_numbers_that_start_with_04() {
        var number = "04"
        while (number.length < 30) {
            assertFalse(validatePhoneNumber(number))
            number = "${number}7"
        }
    }

    @Test
    fun fails_all_numbers_that_start_with_06() {
        var number = "06"
        while (number.length < 30) {
            assertFalse(validatePhoneNumber(number))
            number = "${number}7"
        }
    }

    @Test
    fun passes_09_numbers_with_11_digits_only() {
        var number = "09"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_0800_numbers_with_10_digits_only() {
        var number = "0800"
        while (number.length < 30) {
            assertSame(
                number.length == 10,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_remaining_08_numbers_with_11_digits_only() {
        var number = "08"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            // concatenate any number but 0 for first 2 digits for '08' test
            number = "${number}7"
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    @Test
    fun passes_019756_numbers_with_12_digits_only() {
        var number = "019756"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_019755_numbers_with_12_digits_only() {
        var number = "019755"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_019467_numbers_with_12_digits_only() {
        var number = "019467"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_017683_numbers_with_12_digits_only() {
        var number = "017683"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_017684_numbers_with_12_digits_only() {
        var number = "017684"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_017687_numbers_with_12_digits_only() {
        var number = "017687"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_013397_numbers_with_12_digits_only() {
        var number = "013397"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_013398_numbers_with_12_digits_only() {
        var number = "013398"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_013873_numbers_with_12_digits_only() {
        var number = "013873"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_015242_numbers_with_12_digits_only() {
        var number = "015242"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_015394_numbers_with_12_digits_only() {
        var number = "015394"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_015395_numbers_with_12_digits_only() {
        var number = "015395"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_015396_numbers_with_12_digits_only() {
        var number = "015396"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_016973_numbers_with_12_digits_only() {
        var number = "016973"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_016974_numbers_with_12_digits_only() {
        var number = "016974"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_07_numbers_with_12_digits_only() {
        var number = "07"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_05_numbers_with_12_digits_only() {
        var number = "05"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_03_numbers_with_12_digits_only() {
        var number = "03"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_02_numbers_with_12_digits_only() {
        var number = "02"
        while (number.length < 30) {
            assertSame(
                number.length == 12,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    @Test
    fun passes_brampton_cumbria_01697_72_numbers_with_10_digits_only() {
        var number = "0169772"
        while (number.length < 30) {
            assertSame(
                number.length == 10,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_73_numbers_with_10_digits_only() {
        var number = "0169773"
        while (number.length < 30) {
            assertSame(
                number.length == 10,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    @Test
    fun passes_brampton_cumbria_01697_2_numbers_with_11_digits_only() {
        var number = "016972"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_5_numbers_with_11_digits_only() {
        var number = "016975"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_6_numbers_with_11_digits_only() {
        var number = "016976"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_8_numbers_with_11_digits_only() {
        var number = "016978"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_9_numbers_with_11_digits_only() {
        var number = "016979"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    @Test
    fun passes_brampton_cumbria_01697_74_numbers_with_11_digits_only() {
        var number = "0169774"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_75_numbers_with_11_digits_only() {
        var number = "0169775"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_76_numbers_with_11_digits_only() {
        var number = "0169776"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_77_numbers_with_11_digits_only() {
        var number = "0169777"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_78_numbers_with_11_digits_only() {
        var number = "0169778"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    @Test
    fun passes_brampton_cumbria_01697_790_numbers_with_11_digits_only() {
        var number = "01697790"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_791_numbers_with_11_digits_only() {
        var number = "01697791"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_792_numbers_with_11_digits_only() {
        var number = "01697792"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_793_numbers_with_11_digits_only() {
        var number = "01697793"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_794_numbers_with_11_digits_only() {
        var number = "01697794"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_795_numbers_with_11_digits_only() {
        var number = "01697795"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_796_numbers_with_11_digits_only() {
        var number = "01697796"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_797_numbers_with_11_digits_only() {
        var number = "01697797"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }

    @Test
    fun passes_brampton_cumbria_01697_798_numbers_with_11_digits_only() {
        var number = "01697798"
        while (number.length < 30) {
            assertSame(
                number.length == 11,
                validatePhoneNumber(number)
            )
            number = "${number}7"
        }
    }
}
