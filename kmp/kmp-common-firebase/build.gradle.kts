import com.gchristov.thecodinglove.gradleplugins.Deps
import java.io.FileInputStream
import java.util.*

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
            value = getApiKey("FIREBASE_API_KEY")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_AUTH_DOMAIN",
            value = getApiKey("FIREBASE_AUTH_DOMAIN")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_PROJECT_ID",
            value = getApiKey("FIREBASE_PROJECT_ID")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_STORAGE_BUCKET",
            value = getApiKey("FIREBASE_STORAGE_BUCKET")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_GCM_SENDER_ID",
            value = getApiKey("FIREBASE_GCM_SENDER_ID")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "FIREBASE_APPLICATION_ID",
            value = getApiKey("FIREBASE_APPLICATION_ID")
        )
    }
}

fun getApiKey(key: String): String {
    val propFile = rootProject.file("./local.properties")
    val properties = Properties()
    properties.load(FileInputStream(propFile))
    return properties.getProperty(key)
}