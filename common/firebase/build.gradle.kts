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
                implementation(npm(libs.npm.firebase.admin.get().name, libs.npm.firebase.admin.get().version!!))
                implementation(npm(libs.npm.google.firestore.get().name, libs.npm.google.firestore.get().version!!))
            }
        }
    }
}