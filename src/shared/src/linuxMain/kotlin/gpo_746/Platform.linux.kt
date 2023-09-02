package gpo_746

class LinuxPlatform : Platform {
    override val name: String = "Linux"
}

actual fun getPlatform(): Platform = LinuxPlatform()
