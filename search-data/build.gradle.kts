import com.gchristov.thecodinglove.gradleplugins.common.envVar

val packageId = "com.gchristov.thecodinglove.searchdata"

plugins {
    id("data-plugin")
    id("build-config-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.commonFirebaseData)
                implementation(projects.commonServiceData)
                implementation(projects.htmlParseData)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.commonServiceTestfixtures)
                implementation(projects.searchTestfixtures)
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
            name = "SEARCH_PRELOAD_PUBSUB_TOPIC",
            value = project.envVar("SEARCH_PRELOAD_PUBSUB_TOPIC")
        )
    }
}