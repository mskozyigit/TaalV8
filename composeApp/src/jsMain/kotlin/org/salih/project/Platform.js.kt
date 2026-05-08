package org.salih.project

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

actual fun hideLoadingBar() {
    js("if (window.hideLoadingBar) window.hideLoadingBar();")
}