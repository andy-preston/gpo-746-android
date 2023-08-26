package gpo_746

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform