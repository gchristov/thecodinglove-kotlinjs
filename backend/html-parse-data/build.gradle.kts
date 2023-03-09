plugins {
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val jsTest by getting {
            dependencies {
                implementation(projects.htmlParseTestfixtures)
            }
        }
    }
}