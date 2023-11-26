plugins {
    id("com.gradle.enterprise") version("3.15.1")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "thecodinglove-kmp"

apply {
    from("modules.gradle.kts")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlways()
    }
}