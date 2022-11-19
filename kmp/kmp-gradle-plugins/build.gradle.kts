plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("kmp-platform-plugin") {
        id = "kmp-platform-plugin"
        implementationClass = "com.gchristov.thecodinglove.kmpgradleplugins.KmpPlatformPlugin"
    }
    plugins.register("kmp-module-plugin") {
        id = "kmp-module-plugin"
        implementationClass = "com.gchristov.thecodinglove.kmpgradleplugins.KmpModulePlugin"
    }
    plugins.register("javascript-browser-executable-plugin") {
        id = "javascript-browser-executable-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.kmpgradleplugins.JavascriptBrowserExecutablePlugin"
    }
    plugins.register("javascript-node-executable-plugin") {
        id = "javascript-node-executable-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.kmpgradleplugins.JavascriptNodeExecutablePlugin"
    }
    plugins.register("build-config-plugin") {
        id = "build-config-plugin"
        implementationClass = "com.gchristov.thecodinglove.kmpgradleplugins.BuildConfigPlugin"
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