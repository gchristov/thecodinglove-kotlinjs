plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.commonServiceData)
                implementation(projects.search)
            }
        }
    }
}