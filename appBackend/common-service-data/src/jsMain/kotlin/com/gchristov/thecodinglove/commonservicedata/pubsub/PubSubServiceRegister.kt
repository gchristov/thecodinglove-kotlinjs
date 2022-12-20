package com.gchristov.thecodinglove.commonservicedata.pubsub

import com.gchristov.thecodinglove.commonservicedata.FirebaseFunctions
import kotlin.js.Promise

interface PubSubServiceRegister {
    fun register(
        topic: String,
        callback: (message: PubSubMessage) -> Promise<Unit>
    )
}

internal class RealPubSubServiceRegister : PubSubServiceRegister {
    override fun register(
        topic: String,
        callback: (message: PubSubMessage) -> Promise<Unit>
    ) = FirebaseFunctions.pubsub.topic(topic).onPublish { message ->
        callback(message.toPubSubMessage())
    }
}