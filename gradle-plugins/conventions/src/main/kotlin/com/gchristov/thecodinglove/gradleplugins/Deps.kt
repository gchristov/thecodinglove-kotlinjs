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
}