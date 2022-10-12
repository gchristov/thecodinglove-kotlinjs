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
    plugins.register("config-plugin") {
        id = "config-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.ConfigPlugin"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20-RC")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.7.20-RC")
    implementation("dev.petuska:npm-publish-gradle-plugin:3.0.3")
    implementation("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.7.0")
}