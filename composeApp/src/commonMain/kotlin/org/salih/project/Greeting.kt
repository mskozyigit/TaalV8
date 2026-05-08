package org.salih.project

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        // Return the requested message in Dutch
        return "Hallo, Wereld!"
    }
}