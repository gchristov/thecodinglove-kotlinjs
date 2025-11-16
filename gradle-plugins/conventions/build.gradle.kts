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

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    // Allows these to be available and applied in the pre-compiled conventions plugin
    implementation(libs.kotlin.serialization.gradlePlugin)
    implementation(libs.buildKonfig.gradlePlugin)
}