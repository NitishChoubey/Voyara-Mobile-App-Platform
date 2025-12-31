package com.ebf.voyara

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform