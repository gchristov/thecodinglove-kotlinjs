plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("kmp-platform-plugin") {
        id = "kmp-platform-plugin"
        implementationClass = "com.gchristov.thecodinglove.kmpgradleplugins.KmpPlatformPlugin"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20-RC")
}