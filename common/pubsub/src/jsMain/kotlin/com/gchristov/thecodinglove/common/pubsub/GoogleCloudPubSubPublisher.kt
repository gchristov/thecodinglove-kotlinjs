package com.gchristov.thecodinglove.common.pubsub

import com.gchristov.thecodinglove.common.kotlin.Buffer
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.kotlin.safeJsCall
import kotlinx.coroutines.await
import kotlinx.serialization.SerializationStrategy

internal class GoogleCloudPubSubPublisher(
    private val pubSub: GoogleCloudPubSubExternals.PubSub
) : PubSubPublisher {

    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "credentials-gcp-app.json"
    }

    override suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
    ) = safeJsCall("Error publishing PubSub JSON") {
        val jsonString = jsonSerializer.json.encodeToString(strategy, body)
        pubSub.topic(topic).publish(Buffer.from(jsonString)).await()
    }
}
