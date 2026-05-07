package org.salih.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform