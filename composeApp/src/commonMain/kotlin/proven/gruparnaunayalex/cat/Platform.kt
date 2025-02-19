package proven.gruparnaunayalex.cat

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform