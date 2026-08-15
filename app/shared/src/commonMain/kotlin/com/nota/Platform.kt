package com.nota

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform