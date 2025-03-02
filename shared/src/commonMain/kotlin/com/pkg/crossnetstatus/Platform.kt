package com.pkg.crossnetstatus

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform