package gpo_746

class TestCases() {

    private val usbSystem = UsbSystemLinux()

    private fun openClose() {
        usbSystem.open(0)
        usbSystem.close()
    }
    
    public fun list(): Map<String, () -> Unit> {
        return mapOf(
            "open-close" to ::openClose
        )
    }
}
