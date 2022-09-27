plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("javascript-platform-plugin") {
        id = "javascript-platform-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.JavascriptPlatformPlugin"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20-RC")
}