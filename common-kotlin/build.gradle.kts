import com.gchristov.thecodinglove.gradleplugins.Deps
import java.io.FileInputStream
import java.util.*

val packageId = "com.gchristov.thecodinglove.commonkotlin"

plugins {
    id("base-plugin")
    id("build-config-plugin")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Kodein.di)
                api(Deps.Kermit.logger)
                api(Deps.Kotlin.coroutinesCore)
                api(Deps.Kotlin.dateTime)
                api(Deps.Kotlin.serialization)
                api(Deps.Uuid.uuid)
                api(Deps.Arrow.core)
                api(Deps.Crypto.mac)
                api(Deps.Crypto.encoding)
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
            name = "SLACK_SIGNING_SECRET",
            value = getLocalSecret(rootProject, "SLACK_SIGNING_SECRET")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN,
            name = "SLACK_REQUEST_VERIFICATION_ENABLED",
            value = getLocalSecret(rootProject, "SLACK_REQUEST_VERIFICATION_ENABLED")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_CLIENT_ID",
            value = getLocalSecret(rootProject, "SLACK_CLIENT_ID")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_CLIENT_SECRET",
            value = getLocalSecret(rootProject, "SLACK_CLIENT_SECRET")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_INTERACTIVITY_PUBSUB_TOPIC",
            value = getLocalSecret(rootProject, "SLACK_INTERACTIVITY_PUBSUB_TOPIC")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_SLASH_COMMAND_PUBSUB_TOPIC",
            value = getLocalSecret(rootProject, "SLACK_SLASH_COMMAND_PUBSUB_TOPIC")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SLACK_MONITORING_URL",
            value = getLocalSecret(rootProject, "SLACK_MONITORING_URL")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "APP_LOG_LEVEL",
            value = getLocalSecret(rootProject, "APP_LOG_LEVEL")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "APP_NETWORK_HTML_LOG_LEVEL",
            value = getLocalSecret(rootProject, "APP_NETWORK_HTML_LOG_LEVEL")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "APP_NETWORK_JSON_LOG_LEVEL",
            value = getLocalSecret(rootProject, "APP_NETWORK_JSON_LOG_LEVEL")
        )
        buildConfigField(
            type = com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            name = "SEARCH_PRELOAD_PUBSUB_TOPIC",
            value = getLocalSecret(rootProject, "SEARCH_PRELOAD_PUBSUB_TOPIC")
        )
    }
}

@Suppress("unused")
fun getLocalSecret(
    rootProject: Project,
    key: String
): String {
    val propFile = rootProject.file("./env.properties")
    val properties = Properties()
    properties.load(FileInputStream(propFile))
    return properties.getProperty(key)
}