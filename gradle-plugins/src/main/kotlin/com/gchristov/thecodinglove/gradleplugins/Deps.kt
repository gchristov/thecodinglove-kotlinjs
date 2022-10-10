package com.gchristov.thecodinglove.gradleplugins

class Deps {
    object Ktor {
        private const val ktorVersion = "1.6.7"
        const val clientCore = "io.ktor:ktor-client-core:$ktorVersion"
        const val clientSerialization = "io.ktor:ktor-client-serialization:$ktorVersion"
        const val clientLogging = "io.ktor:ktor-client-logging:$ktorVersion"
        const val clientJavascript = "io.ktor:ktor-client-js:$ktorVersion"
        const val logbackClassic = "ch.qos.logback:logback-classic:1.2.10"
    }
}