plugins {
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.kmpCommonFirebase)
                implementation(projects.commonServiceData)
                implementation(projects.htmlParseData)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(projects.commonServiceTestfixtures)
                implementation(projects.searchTestfixtures)
            }
        }
    }
}