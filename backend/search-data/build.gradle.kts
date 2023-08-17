plugins {
    id("kmp-data-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kmpCommonFirebase)
                implementation(projects.commonServiceData)
                implementation(projects.htmlParseData)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.commonServiceTestfixtures)
                implementation(projects.searchTestfixtures)
            }
        }
    }
}