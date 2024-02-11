plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.network)
                implementation(projects.searchnew.adapter)
                implementation(projects.searchnew.domain)
            }
        }
    }
}