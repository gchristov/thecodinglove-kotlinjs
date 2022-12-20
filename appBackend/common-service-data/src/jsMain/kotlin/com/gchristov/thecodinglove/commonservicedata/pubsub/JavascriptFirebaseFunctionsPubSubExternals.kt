package com.gchristov.thecodinglove.commonservicedata.pubsub

import kotlin.js.Promise

internal external object FirebaseFunctionsPubSub {
    fun topic(name: String): FirebaseFunctionsPubSubTopic
}

internal external object FirebaseFunctionsPubSubTopic {
    fun onPublish(callback: (message: FirebaseFunctionsPubSubMessage) -> Promise<Unit>)
}

internal external class FirebaseFunctionsPubSubMessage {
    val json: dynamic
}