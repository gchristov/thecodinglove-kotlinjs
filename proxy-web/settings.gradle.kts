enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-plugins")
}

rootProject.name = "proxy-web"

include("service")
