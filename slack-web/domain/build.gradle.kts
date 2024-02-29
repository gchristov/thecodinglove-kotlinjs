import com.gchristov.thecodinglove.gradleplugins.Deps
import com.gchristov.thecodinglove.gradleplugins.envSecret

val packageId = "com.gchristov.thecodinglove.slackweb.domain"

plugins {
    id("node-module-plugin")
    id("build-config-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Common.kotlin)
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
            name = "SLACK_CLIENT_ID",
            value = project.envSecret("SLACK_CLIENT_ID")
        )
    }
}