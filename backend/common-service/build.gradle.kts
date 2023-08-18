plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.commonServiceData)
            }
        }
    }
}