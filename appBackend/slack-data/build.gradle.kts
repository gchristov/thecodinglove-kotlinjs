plugins {
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.firebasefunctions)
                implementation(projects.searchData)
            }
        }
    }
}