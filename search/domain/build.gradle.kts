plugins {
    id("node-module-plugin")
}

kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(projects.search.testFixtures)
            }
        }
    }
}