plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("javascript-browser-executable-plugin") {
        id = "javascript-browser-executable-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.gradleplugins.JavascriptBrowserExecutablePlugin"
    }
    plugins.register("javascript-node-executable-plugin") {
        id = "javascript-node-executable-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.gradleplugins.JavascriptNodeExecutablePlugin"
    }
    plugins.register("javascript-node-platform-plugin") {
        id = "javascript-node-platform-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.gradleplugins.JavascriptNodePlatformPlugin"
    }
    plugins.register("javascript-node-module-plugin") {
        id = "javascript-node-module-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.JavascriptNodeModulePlugin"
    }
    plugins.register("build-config-plugin") {
        id = "build-config-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.BuildConfigPlugin"
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