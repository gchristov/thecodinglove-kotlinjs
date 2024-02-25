enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-plugins")
}

rootProject.name = "search"

includeBuild("../common")
include("adapter")
include("domain")
include("service")
include("test-fixtures")
