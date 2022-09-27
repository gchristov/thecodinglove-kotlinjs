plugins {
    id("kmp-platform-plugin")
}

kotlin {
    js(IR) {
        nodejs()
    }
}

dependencies {
    testImplementation(kotlin("test"))
}