plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.gchristov.thecodinglove.common:kotlin")
                implementation("com.gchristov.thecodinglove.common:network")
                implementation("com.gchristov.thecodinglove.common:pubsub")
                implementation("com.gchristov.thecodinglove.common:firebase")
                implementation(projects.domain)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("com.gchristov.thecodinglove.common:test")
                implementation("com.gchristov.thecodinglove.common:network-testfixtures")
                implementation("com.gchristov.thecodinglove.common:pubsub-testfixtures")
                implementation(projects.testFixtures)
            }
        }
    }
}