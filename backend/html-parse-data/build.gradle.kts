plugins {
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(projects.htmlParseTestfixtures)
            }
        }
    }
}