import com.gchristov.thecodinglove.gradleplugins.Deps
import com.gchristov.thecodinglove.gradleplugins.getLocalSecret

val packageId = "com.gchristov.thecodinglove.kmpcommonfirebase"

plugins {
    id("kmp-module-plugin")
    id("build-config-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Google.firebaseFirestore)
            }
        }
    }
}

buildkonfig {
    packageName = packageId
    defaultConfigs {
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_API_KEY",
            value = getLocalSecret(rootProject, "FIREBASE_API_KEY")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_AUTH_DOMAIN",
            value = getLocalSecret(rootProject, "FIREBASE_AUTH_DOMAIN")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_PROJECT_ID",
            value = getLocalSecret(rootProject, "FIREBASE_PROJECT_ID")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_STORAGE_BUCKET",
            value = getLocalSecret(rootProject, "FIREBASE_STORAGE_BUCKET")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_GCM_SENDER_ID",
            value = getLocalSecret(rootProject, "FIREBASE_GCM_SENDER_ID")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_APPLICATION_ID",
            value = getLocalSecret(rootProject, "FIREBASE_APPLICATION_ID")
        )
    }
}