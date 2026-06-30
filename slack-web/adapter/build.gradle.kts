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
                implementation(projects.domain)
                implementation(libs.kotlin.inject.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.common.analytics.testFixtures)
                implementation(libs.common.test)
                implementation(libs.common.network.testFixtures)
            }
        }
    }
}