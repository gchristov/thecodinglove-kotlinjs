plugins {
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.kmpCommonFirebase)
                implementation(projects.htmlParse)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(projects.searchTestfixtures)
            }
        }
    }
}