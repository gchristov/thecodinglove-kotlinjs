enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("../gradle-plugins")
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
