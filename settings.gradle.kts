rootProject.name = "thecodinglove-kotlinjs"

// Add or remove projects here from the common build. Alternatively, each project can be opened in isolation.
includeBuild("common")
includeBuild("gradle-plugins")
includeBuild("landing-page-web")
includeBuild("proxy-web")
includeBuild("search")
includeBuild("self-destruct")
includeBuild("slack")
includeBuild("slack-web")
includeBuild("statistics")

plugins {
    id("com.gradle.develocity") version("3.18.1")
}

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
        publishing.onlyIf { true }
    }
}