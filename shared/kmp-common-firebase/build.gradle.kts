import com.gchristov.thecodinglove.gradleplugins.Deps

plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Google.firebaseFirestore)
            }
        }
    }
}