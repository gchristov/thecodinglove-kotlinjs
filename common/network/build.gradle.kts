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
                api(Deps.Ktor.client)
                implementation(Deps.Ktor.contentNegotiation)
                implementation(Deps.Ktor.serialization)
                implementation(Deps.Ktor.logging)
                implementation(Deps.Ktor.logback)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm(Deps.Node.express.name, Deps.Node.express.version))
            }
        }
    }
}