import com.gchristov.thecodinglove.gradleplugins.Deps
import com.gchristov.thecodinglove.gradleplugins.getLocalSecret

val packageId = "com.gchristov.thecodinglove.kmpcommonnetwork"

plugins {
    id("kmp-module-plugin")
    id("build-config-plugin")
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

buildkonfig {
    packageName = packageId
    defaultConfigs {
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "APP_NETWORK_LOG_LEVEL",
            value = getLocalSecret(rootProject, "APP_NETWORK_LOG_LEVEL")
        )
    }
}