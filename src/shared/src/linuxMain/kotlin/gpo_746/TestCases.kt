package gpo_746

class TestCases() {

    private val ch340g = Ch340g(UsbSystemLinux())

    private fun openClose() {
        ch340g.open()
        ch340g.close()
    }
    
    public fun list(): Map<String, () -> Unit> {
        return mapOf(
            "open-close" to ::openClose
        )
    }
}
