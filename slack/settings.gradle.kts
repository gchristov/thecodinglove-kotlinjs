enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-plugins")
}

plugins {
    id("com.gradle.develocity") version("3.18.1")
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
        publishing.onlyIf { true }
    }
}

rootProject.name = "slack"

includeBuild("../common")
include("adapter")
include("domain")
include("service")