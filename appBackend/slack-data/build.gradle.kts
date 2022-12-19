import com.gchristov.thecodinglove.gradleplugins.getLocalSecret

val packageId = "com.gchristov.thecodinglove.slackdata"

plugins {
    id("kmp-data-plugin")
    id("build-config-plugin")
}

buildkonfig {
    packageName = packageId
    defaultConfigs {
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_SIGNING_SECRET",
            value = getLocalSecret(rootProject, "SLACK_SIGNING_SECRET")
        )
    }
}