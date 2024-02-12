plugins {
    id("module-plugin")
}

kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(projects.common.networkTestfixtures)
                implementation(projects.search.testFixtures)
            }
        }
    }
}