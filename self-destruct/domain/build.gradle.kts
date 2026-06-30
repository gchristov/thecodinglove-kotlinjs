plugins {
    alias(libs.plugins.thecodinglove.node.module)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.common.kotlin)
                implementation(libs.kotlin.inject.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.common.test)
                implementation(projects.testFixtures)
            }
        }
    }
}