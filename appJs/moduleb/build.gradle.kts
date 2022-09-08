plugins {
    kotlin("js") version "1.7.10"
}

kotlin {
    js(IR) {
        nodejs()
    }
}