plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("javascript-application-plugin") {
        id = "javascript-application-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.JavascriptApplicationPlugin"
    }
    plugins.register("javascript-library-plugin") {
        id = "javascript-library-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.JavascriptLibraryPlugin"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20-RC")
}