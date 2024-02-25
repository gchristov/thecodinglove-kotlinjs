plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.gchristov.thecodinglove.common:kotlin")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("com.gchristov.thecodinglove.common:test")
            }
        }
    }
}