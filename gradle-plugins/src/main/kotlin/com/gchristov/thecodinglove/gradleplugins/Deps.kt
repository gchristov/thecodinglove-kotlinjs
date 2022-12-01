package com.gchristov.thecodinglove.gradleplugins

@Suppress("unused")
class Deps {
    object Arrow {
        const val core = "io.arrow-kt:arrow-core:1.1.3"
    }

    object Firebase {
        const val firestore = "dev.gitlive:firebase-firestore:1.6.2"
        val firebase = NpmDependency("firebase", "9.10.0")
        val admin = NpmDependency("firebase-admin", "11.0.1")
        val functions = NpmDependency("firebase-functions", "3.24.0")
    }

    object Kodein {
        const val di = "org.kodein.di:kodein-di:7.15.0"
    }

    object Kotlin {
        private const val coroutinesCoreVersion = "1.6.0"
        private const val coroutinesTestVersion = "1.6.4"

        // "-native-mt" is required here, otherwise iOS fails with runtime exception
        const val coroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesCoreVersion-native-mt"
        const val coroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${coroutinesTestVersion}"
        const val test = "org.jetbrains.kotlin:kotlin-test"
    }

    object Ktor {
        private const val ktorVersion = "2.1.2"
        const val client = "io.ktor:ktor-client-core:$ktorVersion"
        const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:$ktorVersion"
        const val serialisation = "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion"
        const val logging = "io.ktor:ktor-client-logging:$ktorVersion"
        const val logback = "ch.qos.logback:logback-classic:1.2.10"
    }

    object Node {
        val htmlParser = NpmDependency("node-html-parser", "6.1.4")
        val express = NpmDependency("express", "4.18.2")
    }

    object Uuid {
        const val uuid = "com.benasher44:uuid:0.6.0"
    }
}

data class NpmDependency(
    val name: String,
    val version: String
)