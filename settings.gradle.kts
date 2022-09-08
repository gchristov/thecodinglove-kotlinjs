pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "thecodinglove-kmp"

apply {
    from("modules.gradle.kts")
}