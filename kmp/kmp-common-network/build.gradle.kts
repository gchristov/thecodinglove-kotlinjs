import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // TODO: This should be private
                api(Deps.Ktor.client)
                implementation(Deps.Ktor.contentNegotiation)
                api(Deps.Ktor.serialisation)
                implementation(Deps.Ktor.logging)
                implementation(Deps.Ktor.logback)
            }
        }
    }
}