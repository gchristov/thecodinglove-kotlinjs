plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins.register("platform-plugin") {
        id = "platform-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.kmp.PlatformPlugin"
    }
    plugins.register("module-plugin") {
        id = "module-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.kmp.ModulePlugin"
    }
    plugins.register("data-plugin") {
        id = "data-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.kmp.DataPlugin"
    }
    plugins.register("build-config-plugin") {
        id = "build-config-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.BuildConfigPlugin"
    }
    plugins.register("web-executable-plugin") {
        id = "web-executable-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.web.WebExecutablePlugin"
    }
    plugins.register("backend-executable-plugin") {
        id = "backend-executable-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.backend.BackendExecutablePlugin"
    }
    plugins.register("backend-service-plugin") {
        id = "backend-service-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.backend.BackendServicePlugin"
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