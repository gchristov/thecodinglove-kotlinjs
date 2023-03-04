import com.gchristov.thecodinglove.gradleplugins.getLocalSecret

val packageId = "com.gchristov.thecodinglove.slackdata"

plugins {
    id("kmp-data-plugin")
    id("build-config-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.commonServiceData)
                implementation(projects.searchData)
            }
        }
    }
}

buildkonfig {
    packageName = packageId
    defaultConfigs {
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_SIGNING_SECRET",
            value = getLocalSecret(rootProject, "SLACK_SIGNING_SECRET")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN,
            name = "SLACK_REQUEST_VERIFICATION_ENABLED",
            value = getLocalSecret(rootProject, "SLACK_REQUEST_VERIFICATION_ENABLED")
        )
    }
}