package com.gchristov.thecodinglove.commonservicedata.pubsub

import com.gchristov.thecodinglove.commonservicedata.FirebaseFunctions
import kotlin.js.Promise

object PubSubServiceRegistrations {
    fun register(
        topic: String,
        callback: (message: PubSubMessage) -> Promise<Unit>
    ) = FirebaseFunctions.pubsub.topic(topic).onPublish { message ->
        callback(message.toPubSubMessage())
    }
}