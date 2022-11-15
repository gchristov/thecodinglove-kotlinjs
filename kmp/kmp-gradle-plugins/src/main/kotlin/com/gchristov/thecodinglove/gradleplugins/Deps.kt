package com.gchristov.thecodinglove.gradleplugins

@Suppress("unused")
class Deps {
    object Kodein {
        private const val kodeinVersion = "7.15.0"
        const val di = "org.kodein.di:kodein-di:$kodeinVersion"
    }

    object Kotlin {
        private const val coroutinesVersion = "1.6.0"

        // "-native-mt" is required here, otherwise iOS fails with runtime exception
        const val coroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion-native-mt"
    }

    object Ktor {
        private const val ktorVersion = "2.1.2"
        const val client = "io.ktor:ktor-client-core:$ktorVersion"
        const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:$ktorVersion"
        const val serialisation = "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion"
        const val logging = "io.ktor:ktor-client-logging:$ktorVersion"
        const val logback = "ch.qos.logback:logback-classic:1.2.10"
    }

    object Firebase {
        private const val firebaseVersion = "1.6.2"
        const val firestore = "dev.gitlive:firebase-firestore:$firebaseVersion"
    }
}