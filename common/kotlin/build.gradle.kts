import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("base-node-plugin")
}

group = "com.gchristov.thecodinglove.common"
version = "0.0.1"

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kodein)
                api(libs.touchlab.kermit)
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.serialization.json)
                api(libs.uuid)
                api(libs.arrow.core)
                api(libs.diglol.crypto)
                api(libs.diglol.encoding)
            }
        }
    }
}