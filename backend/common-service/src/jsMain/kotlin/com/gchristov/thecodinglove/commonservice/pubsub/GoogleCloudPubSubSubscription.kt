package com.gchristov.thecodinglove.commonservice.pubsub

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSubscription
import com.gchristov.thecodinglove.kmpcommonkotlin.AppConfig
import com.gchristov.thecodinglove.kmpcommonkotlin.process
import kotlinx.coroutines.await
import kotlin.js.json

internal class GoogleCloudPubSubSubscription(
    private val log: Logger,
    private val pubSub: GoogleCloudPubSubExternals.PubSub,
    private val appConfig: AppConfig,
) : PubSubSubscription {
    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-gcp.json"
    }

    override suspend fun initialise(
        topic: String,
        httpPath: String,
    ): Either<Throwable, Unit> = try {
        val pubSubTopic = pubSub.topic(topic)
        if (!pubSubTopic.exists().await().first()) {
            log.d("Creating PubSub topic $topic")
            pubSubTopic.create().await()
        }
        val subscription = "${topic}_subscription"
        val pubSubSubscription = pubSubTopic.subscription(subscription)
        if (!pubSubSubscription.exists().await().first()) {
            log.d("Creating PubSub subscription $subscription")
            val trimmedDomain = appConfig.publicUrl
            val trimmedPath = httpPath.removePrefix("/")
            pubSubSubscription.create(
                // Docs - https://cloud.google.com/nodejs/docs/reference/pubsub/latest
                json(
                    "pushEndpoint" to "$trimmedDomain/$trimmedPath",
                    "messageRetentionDuration" to json(
                        "seconds" to "600s"
                    ),
                    "retryPolicy" to json(
                        "minimumBackoff" to json(
                            "seconds" to 10
                        ),
                        "maximumBackoff" to json(
                            "seconds" to 600
                        )
                    ),
                    "expirationPolicy" to json(
                        "ttl" to null
                    )
                )
            ).await()
        }
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error initialising PubSub subscription${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}