plugins {
    id("javascript-platform-plugin")
}

kotlin {
    js(IR) {
        nodejs()
    }
}

dependencies {
    testImplementation(kotlin("test"))
}