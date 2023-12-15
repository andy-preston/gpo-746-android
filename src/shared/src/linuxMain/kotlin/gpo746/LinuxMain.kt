import andyp.gpo746.Ch340g
import andyp.gpo746.TestCases
import andyp.gpo746.UsbSystemMock
import platform.posix.sleep

private const val DEFAULT_PAUSE = 10u
private const val DEFAULT_COUNT = 1

fun main(args: Array<String>) {
    val ch340g = Ch340g(UsbSystemMock())
    val testCases = TestCases(ch340g)
    val testMap = testCases.list()
    val requiredTest: String = if (args.size > 0) args[0] else ""
    val repeats: Int = if (args.size > 1) args[1].toInt() else DEFAULT_COUNT
    val pause: UInt = if (args.size > 2) args[2].toUInt() else DEFAULT_PAUSE
    if (testMap.contains(requiredTest)) {
        ch340g.start()
        val testMethod = testMap[requiredTest]
        for (repeat in 1..repeats) {
            if (repeat > 1) {
                sleep(pause)
            }
            testMethod!!.invoke()
        }
        ch340g.finish()
    } else {
        println("./bin/usb-test test-case [number to repeat] [pause between tests]")
        println("Available test cases:")
        testMap.keys.forEach { testName ->
            println(testName)
        }
        println("default number of repeats is 1")
        println("default pause is 10")
    }
}
