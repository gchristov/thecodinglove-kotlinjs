import com.gchristov.thecodinglove.gradleplugins.common.envVar

val packageId = "com.gchristov.thecodinglove.search.adapter"

plugins {
    id("module-plugin")
    id("build-config-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.common.network)
                implementation(projects.common.pubsub)
                implementation(projects.common.firebase)
                implementation(projects.searchnew.domain)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.common.networkTestfixtures)
                implementation(projects.common.pubsubTestfixtures)
                implementation(projects.searchnew.testFixtures)
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