plugins {
    id("kmp-module-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(projects.htmlParseData)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(projects.htmlParseTestfixtures)
            }
        }
    }
}