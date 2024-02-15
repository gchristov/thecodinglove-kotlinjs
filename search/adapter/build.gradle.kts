plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.network)
                implementation(projects.common.pubsub)
                implementation(projects.common.firebase)
                implementation(projects.search.domain)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.common.networkTestfixtures)
                implementation(projects.common.pubsubTestfixtures)
                implementation(projects.search.testFixtures)
            }
        }
    }
}