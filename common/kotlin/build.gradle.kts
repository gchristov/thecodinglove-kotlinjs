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
                api(Deps.Kodein.di)
                api(Deps.Kermit.logger)
                api(Deps.Kotlin.coroutinesCore)
                api(Deps.Kotlin.dateTime)
                api(Deps.Kotlin.serialization)
                api(Deps.Uuid.uuid)
                api(Deps.Arrow.core)
                api(Deps.Crypto.mac)
                api(Deps.Crypto.encoding)
            }
        }
    }
}