import com.gchristov.thecodinglove.gradleplugins.common.envVar

val packageId = "com.gchristov.thecodinglove.slackdata"

plugins {
    id("data-plugin")
    id("build-config-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.firebase)
                implementation(projects.common.network)
                implementation(projects.searchData)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.slackTestfixtures)
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
            value = project.envVar("SLACK_SIGNING_SECRET")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN,
            name = "SLACK_REQUEST_VERIFICATION_ENABLED",
            value = project.envVar("SLACK_REQUEST_VERIFICATION_ENABLED")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_CLIENT_ID",
            value = project.envVar("SLACK_CLIENT_ID")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_CLIENT_SECRET",
            value = project.envVar("SLACK_CLIENT_SECRET")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_INTERACTIVITY_PUBSUB_TOPIC",
            value = project.envVar("SLACK_INTERACTIVITY_PUBSUB_TOPIC")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_SLASH_COMMAND_PUBSUB_TOPIC",
            value = project.envVar("SLACK_SLASH_COMMAND_PUBSUB_TOPIC")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_MONITORING_URL",
            value = project.envVar("SLACK_MONITORING_URL")
        )
    }
}