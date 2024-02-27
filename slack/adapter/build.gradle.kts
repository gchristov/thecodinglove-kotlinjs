import com.gchristov.thecodinglove.gradleplugins.Deps
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
                implementation(Deps.Common.kotlin)
                implementation(Deps.Common.network)
                implementation(Deps.Common.pubsub)
                implementation(Deps.Common.firebase)
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