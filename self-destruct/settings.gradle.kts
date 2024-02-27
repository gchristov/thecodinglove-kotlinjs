enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-plugins")
}

rootProject.name = "self-destruct"

includeBuild("../common")
include("adapter")
include("domain")
include("service")
