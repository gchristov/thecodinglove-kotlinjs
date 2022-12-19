package com.gchristov.thecodinglove.commonservicedata.pubsub

import com.gchristov.thecodinglove.commonservicedata.api.FirebaseFunctions
import kotlin.js.Promise

object PubSubServiceRegistrations {
    private val pubSubMessageFacade = PubSubMessageFacade()

    fun register(
        topic: String,
        callback: (message: PubSubMessage) -> Promise<Unit>
    ) = FirebaseFunctions.pubsub.topic(topic).onPublish { message ->
        callback(pubSubMessageFacade(message))
    }
}

private class PubSubMessageFacade {
    operator fun invoke(
        message: FirebaseFunctionsPubSubMessage
    ): PubSubMessage = object : PubSubMessage {
        override val json: Any = message.json as Any
    }
}