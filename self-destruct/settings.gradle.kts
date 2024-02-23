pluginManagement {
    includeBuild("../gradle-plugins")
}

rootProject.name = "self-destruct"

includeBuild("../common")
include("adapter")
include("domain")
include("service")
