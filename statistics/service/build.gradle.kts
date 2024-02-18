plugins {
    id("backend-binary-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.search.proto)
                implementation(projects.statistics.domain)
                implementation(projects.statistics.adapter)
            }
        }
    }
}