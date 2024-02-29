import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("node-module-plugin")
}

group = "com.gchristov.thecodinglove.common"
version = "0.0.1"

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kotlin)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm(Deps.Google.firebaseAdmin.name, Deps.Google.firebaseAdmin.version))
            }
        }
    }
}