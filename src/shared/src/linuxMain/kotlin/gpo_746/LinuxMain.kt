import gpo_746.TestCases

fun main(args: Array<String>) {
    val testCases = TestCases().list()
    if (args.size == 0) {
        println("Available test cases:")
        testCases.forEach { (testName, testMethod) ->
            println(testName)
        }
    } else {
        args.forEach { argument ->
            testCases.forEach { (testName, testMethod) ->
                if (argument == testName) {
                    testMethod()
                }
            }
        }
    }
}
