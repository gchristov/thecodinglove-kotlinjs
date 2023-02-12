plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(projects.commonServiceData)
            }
        }
    }
}