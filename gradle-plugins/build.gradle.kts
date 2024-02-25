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
    plugins.register("node-module-plugin") {
        id = "node-module-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.NodeModulePlugin"
    }
    plugins.register("build-config-plugin") {
        id = "build-config-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.BuildConfigPlugin"
    }
    plugins.register("browser-binary-plugin") {
        id = "browser-binary-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.BrowserBinaryPlugin"
    }
    plugins.register("node-binary-plugin") {
        id = "node-binary-plugin"
        implementationClass = "com.gchristov.thecodinglove.gradleplugins.NodeBinaryPlugin"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.9.21")
    implementation("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.15.0")
}

val taskNames = listOf("clean", "assemble")
taskNames.forEach {  taskName ->
    tasks.register("${taskName}All") {
        tasks.findByName(taskName)?.let { dependsOn(it) }
        dependsOn(gradle.includedBuilds.map { it.task(":${taskName}All") })
        subprojects.map { it.tasks.findByName(taskName)?.let { dependsOn(it) } }
    }
}