package com.gchristov.thecodinglove.gradleplugins

class Deps {
    object Common {
        private const val group = "com.gchristov.thecodinglove.common"
        const val analytics = "$group:analytics"
        const val analyticsTestFixtures = "$group:analytics-testfixtures"
        const val firebase = "$group:firebase"
        const val kotlin = "$group:kotlin"
        const val monitoring = "$group:monitoring"
        const val network = "$group:network"
        const val networkTestFixtures = "$group:network-testfixtures"
        const val pubsub = "$group:pubsub"
        const val pubsubTestFixtures = "$group:pubsub-testfixtures"
        const val test = "$group:test"
    }

    object Google {
        val firebaseAdmin = NpmDependency("firebase-admin", "11.11.1")
        val firestore = NpmDependency("@google-cloud/firestore", "6.8.0")
        val pubSub = NpmDependency("@google-cloud/pubsub", "4.0.7")
    }

    object Node {
        val htmlParser = NpmDependency("node-html-parser", "6.1.11")
        val express = NpmDependency("express", "4.18.2")
    }
}

data class NpmDependency(
    val name: String,
    val version: String
)