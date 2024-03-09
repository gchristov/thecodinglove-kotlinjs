import com.gchristov.thecodinglove.gradleplugins.envSecret

val packageId = "com.gchristov.thecodinglove.common.analytics"

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
            name = "GOOGLE_ANALYTICS_MEASUREMENT_ID",
            value = project.envSecret("GOOGLE_ANALYTICS_MEASUREMENT_ID")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "GOOGLE_ANALYTICS_API_SECRET",
            value = project.envSecret("GOOGLE_ANALYTICS_API_SECRET")
        )
    }
}