package com.gchristov.thecodinglove.common.pubsub

import com.gchristov.thecodinglove.common.kotlin.Buffer
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.kotlin.safeJsCall
import kotlinx.coroutines.await
import kotlinx.serialization.SerializationStrategy
import kotlin.js.json
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

// Must match the queue provisioned in common/infra/Pulumi.yaml.
private const val CloudTasksQueueLocation = "europe-west1"
private const val CloudTasksQueueName = "scheduled-events"
private const val PubSubOAuthScope = "https://www.googleapis.com/auth/pubsub"

internal class GoogleCloudPubSubPublisher(
    private val pubSub: GoogleCloudPubSubExternals.PubSub,
    private val cloudTasks: GoogleCloudTasksExternals.CloudTasks,
) : PubSubPublisher {

    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "credentials-gcp-app.json"
    }

    override suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
        delay: Duration,
    ) = if (delay > Duration.ZERO) {
        scheduleJson(topic, body, jsonSerializer, strategy, delay)
    } else {
        publishJsonNow(topic, body, jsonSerializer, strategy)
    }

    private suspend fun <T> publishJsonNow(
        topic: String,
        body: T,
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
    ) = safeJsCall("Error publishing PubSub JSON") {
        val jsonString = jsonSerializer.json.encodeToString(strategy, body)
        pubSub.topic(topic).publish(Buffer.from(jsonString)).await()
    }

    // Publishing a delayed message isn't natively supported by PubSub, so instead of publishing
    // directly we schedule a Cloud Task whose HTTP target is PubSub's own REST publish endpoint,
    // authenticated as this service's own identity via an OAuth token. The task fires (and
    // therefore the message actually publishes) once the delay has elapsed.
    @OptIn(ExperimentalTime::class)
    private suspend fun <T> scheduleJson(
        topic: String,
        body: T,
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
        delay: Duration,
    ) = safeJsCall("Error scheduling delayed PubSub publish") {
        val jsonString = jsonSerializer.json.encodeToString(strategy, body)
        val messageData = Buffer.from(jsonString).toString("base64")
        val publishRequestBody = """{"messages":[{"data":"$messageData"}]}"""

        val projectId = cloudTasks.auth.getProjectId().await()
        val serviceAccountEmail = cloudTasks.auth.getCredentials().await().client_email as String
        // Kotlin Long isn't a plain JS number - the gRPC client's protobuf encoder can't serialize
        // it as-is (fails with "invalid encoding"), so convert to a native JS number first.
        val scheduleSeconds = (Clock.System.now() + delay).epochSeconds.toDouble()

        val task = json(
            "httpRequest" to json(
                "httpMethod" to "POST",
                "url" to "https://pubsub.googleapis.com/v1/projects/$projectId/topics/$topic:publish",
                "headers" to json("Content-Type" to "application/json"),
                // A plain string here is ambiguous for a protobuf bytes field (some encoders treat
                // it as base64), so pass real bytes instead of the raw JSON text.
                "body" to Buffer.from(publishRequestBody),
                "oauthToken" to json(
                    "serviceAccountEmail" to serviceAccountEmail,
                    "scope" to PubSubOAuthScope,
                ),
            ),
            "scheduleTime" to json("seconds" to scheduleSeconds),
        )
        val queuePath = cloudTasks.queuePath(projectId, CloudTasksQueueLocation, CloudTasksQueueName)
        val response = cloudTasks.createTask(json("parent" to queuePath, "task" to task)).await()
        response[0].name as String
    }
}
