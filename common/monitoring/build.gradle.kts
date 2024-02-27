import com.gchristov.thecodinglove.gradleplugins.envSecret

val packageId = "com.gchristov.thecodinglove.common.monitoring"

plugins {
    id("node-module-plugin")
    id("build-config-plugin")
}

group = "com.gchristov.thecodinglove.common"
version = "0.0.1"

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kotlin)
                implementation(projects.network)
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
            name = "MONITORING_SLACK_URL",
            value = project.envSecret("MONITORING_SLACK_URL")
        )
    }
}