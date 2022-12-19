package com.gchristov.thecodinglove.commonservicedata.pubsub

import com.gchristov.thecodinglove.commonservicedata.api.FirebaseFunctions
import kotlin.js.Promise

object PubSubServiceRegistrations {
    fun register(
        topic: String,
        callback: (message: PubSubMessage) -> Promise<Unit>
    ) = FirebaseFunctions.pubsub.topic(topic).onPublish { message ->
        callback(message.toPubSubMessage())
    }
}

private fun FirebaseFunctionsPubSubMessage.toPubSubMessage() = object : PubSubMessage {
    override val json: Any = this@toPubSubMessage.json as Any
}