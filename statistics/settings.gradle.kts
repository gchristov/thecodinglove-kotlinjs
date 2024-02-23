pluginManagement {
    includeBuild("../gradle-plugins")
}

rootProject.name = "statistics"

includeBuild("../common")
include("adapter")
include("domain")
include("service")
