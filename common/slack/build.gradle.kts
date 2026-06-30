plugins {
    alias(libs.plugins.thecodinglove.node.module)
}

group = "com.gchristov.thecodinglove.common"
version = "0.0.1"

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kotlin)
                implementation(projects.network)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.test)
            }
        }
    }
}
