package org.salih.project

import kotlinx.browser.window
import kotlinx.coroutines.await

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

actual fun hideLoadingBar() {
    js("if (window.hideLoadingBar) window.hideLoadingBar();")
}

actual suspend fun loadJsonFile(path: String): String {
    val response = window.fetch(path).await()
    return response.text().await() as String
}