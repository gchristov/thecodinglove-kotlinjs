package com.gchristov.thecodinglove.gradleplugins

class Deps {
    object Common {
        private const val group = "com.gchristov.thecodinglove.common"
        const val firebase = "$group:firebase"
        const val kotlin = "$group:kotlin"
        const val monitoring = "$group:monitoring"
        const val network = "$group:network"
        const val networkTestFixtures = "$group:network-testfixtures"
        const val pubsub = "$group:pubsub"
        const val pubsubTestFixtures = "$group:pubsub-testfixtures"
        const val test = "$group:test"
    }

    object Arrow {
        const val core = "io.arrow-kt:arrow-core:1.2.1"
    }

    object Crypto {
        const val mac = "com.diglol.crypto:mac:0.1.5"
        const val encoding = "com.diglol.encoding:encoding:0.3.0"
    }

    object Google {
        val firebaseAdmin = NpmDependency("firebase-admin", "11.11.1")
        val pubSub = NpmDependency("@google-cloud/pubsub", "4.0.7")
    }

    object Kermit {
        private const val kermitVersion = "2.0.2"
        const val logger = "co.touchlab:kermit:$kermitVersion"
    }

    object Kodein {
        const val di = "org.kodein.di:kodein-di:7.21.0"
    }

    object Kotlin {
        private const val coroutinesVersion = "1.7.3"

        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${coroutinesVersion}"
        const val test = "org.jetbrains.kotlin:kotlin-test"
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.1"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1"
    }

    object Ktor {
        private const val ktorVersion = "2.3.6"
        const val client = "io.ktor:ktor-client-core:$ktorVersion"
        const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:$ktorVersion"
        const val serialization = "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion"
        const val logging = "io.ktor:ktor-client-logging:$ktorVersion"
        const val logback = "ch.qos.logback:logback-classic:1.4.11"
    }

    object Node {
        val htmlParser = NpmDependency("node-html-parser", "6.1.11")
        val express = NpmDependency("express", "4.18.2")
    }

    object Uuid {
        const val uuid = "com.benasher44:uuid:0.8.2"
    }
}

data class NpmDependency(
    val name: String,
    val version: String
)