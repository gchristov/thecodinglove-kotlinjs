import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Ktor.client)
                implementation(Deps.Ktor.contentNegotiation)
                implementation(Deps.Ktor.serialization)
                implementation(Deps.Ktor.logging)
                implementation(Deps.Ktor.logback)
            }
        }
    }
}