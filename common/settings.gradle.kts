pluginManagement {
    includeBuild("../gradle-plugins")
}

rootProject.name = "common"

include("firebase")
include("kotlin")
include("monitoring")
include("network")
include("network-testfixtures")
include("pubsub")
include("pubsub-testfixtures")
include("test")