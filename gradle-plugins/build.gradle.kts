plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("base-node-plugin") {
        id = "base-node-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.BaseNodePlugin"
    }
    plugins.register("base-browser-plugin") {
        id = "base-browser-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.BaseBrowserPlugin"
    }
    plugins.register("module-plugin") {
        id = "module-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.ModulePlugin"
    }
    plugins.register("build-config-plugin") {
        id = "build-config-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.BuildConfigPlugin"
    }
    plugins.register("frontend-binary-plugin") {
        id = "frontend-binary-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.FrontendBinaryPlugin"
    }
    plugins.register("backend-binary-plugin") {
        id = "backend-binary-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.BackendBinaryPlugin"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.9.21")
    implementation("dev.petuska:npm-publish-gradle-plugin:3.4.1")
    implementation("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.15.0")
}