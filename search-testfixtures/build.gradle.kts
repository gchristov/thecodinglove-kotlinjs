plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.commonServiceData)
                implementation(projects.searchData)
            }
        }
    }
}