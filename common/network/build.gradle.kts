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
                api(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.serializationJson)
                implementation(libs.ktor.client.logging)
                implementation(libs.logback)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm(libs.npm.express.get().name, libs.npm.express.get().version!!))
            }
        }
    }
}