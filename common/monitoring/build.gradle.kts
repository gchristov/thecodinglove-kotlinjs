import com.gchristov.thecodinglove.gradleplugins.envSecret

val packageId = "com.gchristov.thecodinglove.common.monitoring"

plugins {
    alias(libs.plugins.thecodinglove.node.module)
    alias(libs.plugins.thecodinglove.build.config)
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