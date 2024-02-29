enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-plugins")
}

rootProject.name = "slack-web"

includeBuild("../common")
include("adapter")
include("domain")
include("service")
