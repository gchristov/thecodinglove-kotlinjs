package com.gchristov.thecodinglove.commonservicedata.pubsub

import kotlin.js.Promise

external object FirebaseFunctionsPubSub {
    fun topic(name: String): FirebaseFunctionsPubSubTopic
}

external object FirebaseFunctionsPubSubTopic {
    fun onPublish(callback: (message: FirebaseFunctionsPubSubMessage) -> Promise<Unit>)
}

external class FirebaseFunctionsPubSubMessage {
    val json: dynamic
}