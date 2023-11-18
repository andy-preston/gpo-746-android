package andyp.gpo746

enum class ValidatorResult {
    Invalid,
    Incomplete,
    Good
}

class PhoneNumberValidator {

    @Suppress("MagicNumber")
    private val codeLengthMap = mapOf(
        "0800" to 10,
        "07" to 12,
        "08" to 11,
        "09" to 11,
        "05" to 12,
        "03" to 12,
        "02" to 12,
        "019756" to 12,
        "019755" to 12,
        "019467" to 12,
        "017683" to 12,
        "017684" to 12,
        "017687" to 12,
        "013397" to 12,
        "013398" to 12,
        "013873" to 12,
        "015242" to 12,
        "015394" to 12,
        "015395" to 12,
        "015396" to 12,
        "016973" to 12,
        "016974" to 12,
        "0169772" to 10,
        "0169773" to 10,
        "016972" to 11,
        "016975" to 11,
        "016976" to 11,
        "016978" to 11,
        "016979" to 11,
        "0169774" to 11,
        "0169775" to 11,
        "0169776" to 11,
        "0169777" to 11,
        "0169778" to 11,
        "01697790" to 11,
        "01697791" to 11,
        "01697792" to 11,
        "01697793" to 11,
        "01697794" to 11,
        "01697795" to 11,
        "01697796" to 11,
        "01697797" to 11,
        "01697798" to 11
    )

    @Suppress("ReturnCount")
    public fun result(number: String): ValidatorResult {
        if (number.length == 0) {
            return ValidatorResult.Incomplete
        }
        if (!number.startsWith("0")) {
            return ValidatorResult.Invalid
        }
        if (number.startsWith("04")) {
            return ValidatorResult.Invalid
        }
        if (number.startsWith("06")) {
            return ValidatorResult.Invalid
        }
        var maximumCodeLength = 0
        codeLengthMap.forEach { (code, digits) ->
            if (code.length > maximumCodeLength) {
                maximumCodeLength = code.length
            }
            if (number.startsWith(code)) {
                if (number.length > digits) {
                    return ValidatorResult.Invalid
                }
                if (number.length < digits) {
                    return ValidatorResult.Incomplete
                }
                return ValidatorResult.Good
            }
        }
        if (number.length > maximumCodeLength) {
            return ValidatorResult.Invalid
        }
        return ValidatorResult.Incomplete
    }
}
