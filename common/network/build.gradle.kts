import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("module-plugin-2")
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