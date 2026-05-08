package org.salih.project

import kotlin.js.Promise
import kotlinx.coroutines.await

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

@JsFun("() => { if (window.hideLoadingBar) window.hideLoadingBar(); }")
external fun hideLoadingBarInternal()

actual fun hideLoadingBar() {
    hideLoadingBarInternal()
}

@JsFun("(url) => fetch(url).then(r => r.text())")
external fun fetchTextInternal(url: String): Promise<JsString>

actual suspend fun loadJsonFile(path: String): String {
    val jsString = fetchTextInternal(path).await<JsString>()
    return jsString.toString()
}