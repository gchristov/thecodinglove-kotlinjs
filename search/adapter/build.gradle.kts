plugins {
    alias(libs.plugins.thecodinglove.node.module)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.common.analytics)
                implementation(libs.common.kotlin)
                implementation(libs.common.network)
                implementation(libs.common.pubsub)
                implementation(libs.common.firebase)
                implementation(projects.domain)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.common.test)
                implementation(libs.common.analytics.testFixtures)
                implementation(libs.common.network.testFixtures)
                implementation(libs.common.pubsub.testFixtures)
                implementation(projects.testFixtures)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm(libs.npm.htmlParser.get().name, libs.npm.htmlParser.get().version!!))
            }
        }
    }
}