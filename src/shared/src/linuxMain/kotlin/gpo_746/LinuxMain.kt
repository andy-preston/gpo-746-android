import gpo_746.Ch340g
import gpo_746.TestCases
import gpo_746.UsbSystemMock
import platform.posix.sleep

fun main(args: Array<String>) {
    val ch340g = Ch340g(UsbSystemMock())
    val testCases = TestCases(ch340g)
    val testMap = testCases.list()
    val requiredTest: String = if (args.size >= 1) args[0] else ""
    val repeats: Int = if (args.size >= 2) args[1].toInt() else 1
    val pause: UInt = if (args.size >= 3) args[2].toUInt() else 10u

    testMap.forEach { (testName, testMethod) ->
        if (requiredTest == testName) {
            ch340g.start()
            for (repeat in 1..repeats) {
                if (repeat > 1) {
                    sleep(pause)
                }
                testMethod()
            }
            ch340g.finish()
            return
        }
    }
    println ("./bin/usb-test test-case [number of repeats] [pause between tests]")
    println("Available test cases:")
    testMap.keys.forEach { testName ->
        println(testName)
    }
    println ("default number of repeats is 1")
    println ("default pause is 10")
}
