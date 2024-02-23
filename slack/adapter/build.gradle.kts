import com.gchristov.thecodinglove.gradleplugins.envSecret

val packageId = "com.gchristov.thecodinglove.slack.adapter"

plugins {
    id("node-module-plugin")
    id("build-config-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.gchristov.thecodinglove.common:network")
                implementation("com.gchristov.thecodinglove.common:pubsub")
                implementation("com.gchristov.thecodinglove.common:firebase")
                implementation(projects.domain)
            }
        }
    }
}

buildkonfig {
    packageName = packageId
    exposeObjectWithName = "BuildConfig"
    defaultConfigs {
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_SIGNING_SECRET",
            value = project.envSecret("SLACK_SIGNING_SECRET")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_CLIENT_ID",
            value = project.envSecret("SLACK_CLIENT_ID")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_CLIENT_SECRET",
            value = project.envSecret("SLACK_CLIENT_SECRET")
        )
    }
}