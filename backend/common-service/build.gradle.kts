import com.gchristov.thecodinglove.gradleplugins.getLocalSecret

val packageId = "com.gchristov.thecodinglove.commonservice"

plugins {
    id("kmp-module-plugin")
    id("build-config-plugin")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(projects.commonServiceData)
            }
        }
    }
}

buildkonfig {
    packageName = packageId
    defaultConfigs {
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "APP_PUBLIC_URL",
            value = getLocalSecret(rootProject, "APP_PUBLIC_URL")
        )
    }
}