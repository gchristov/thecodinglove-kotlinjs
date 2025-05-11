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
}

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
        publishing.onlyIf { true }
    }
}

rootProject.name = "common"

include("analytics")
include("analytics-testfixtures")
include("firebase")
include("kotlin")
include("monitoring")
include("network")
include("network-testfixtures")
include("pubsub")
include("pubsub-testfixtures")
include("test")