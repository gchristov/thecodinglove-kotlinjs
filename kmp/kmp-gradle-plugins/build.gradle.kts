plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("javascript-browser-executable-plugin") {
        id = "javascript-browser-executable-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.gradleplugins.js.JavascriptBrowserExecutablePlugin"
    }
    plugins.register("javascript-node-library-plugin") {
        id = "javascript-node-library-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.gradleplugins.js.JavascriptNodeLibraryPlugin"
    }
    plugins.register("javascript-node-target-plugin") {
        id = "javascript-node-target-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.gradleplugins.js.JavascriptNodeTargetPlugin"
    }
    plugins.register("javascript-node-module-plugin") {
        id = "javascript-node-module-plugin"
        implementationClass =
            "com.gchristov.thecodinglove.gradleplugins.js.JavascriptNodeModulePlugin"
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