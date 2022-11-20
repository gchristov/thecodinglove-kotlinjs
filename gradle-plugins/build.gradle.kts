plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("kmp-platform-plugin") {
        id = "kmp-platform-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.kmp.KmpPlatformPlugin"
    }
    plugins.register("kmp-module-plugin") {
        id = "kmp-module-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.kmp.KmpModulePlugin"
    }
    plugins.register("kmp-data-plugin") {
        id = "kmp-data-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.kmp.KmpDataPlugin"
    }
    plugins.register("build-config-plugin") {
        id = "build-config-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.BuildConfigPlugin"
    }
    plugins.register("javascript-browser-executable-plugin") {
        id = "javascript-browser-executable-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.gradleplugins.js.JavascriptBrowserExecutablePlugin"
    }
    plugins.register("javascript-node-executable-plugin") {
        id = "javascript-node-executable-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.gradleplugins.js.JavascriptNodeExecutablePlugin"
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