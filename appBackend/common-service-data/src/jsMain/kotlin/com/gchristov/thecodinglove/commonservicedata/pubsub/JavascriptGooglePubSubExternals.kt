// This is needed, otherwise we get "PubSub is not a constructor" error
@file:JsModule("@google-cloud/pubsub")
@file:JsNonModule
package com.gchristov.thecodinglove.commonservicedata.pubsub

import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer

external class PubSub(projectId: String) {
    fun topic(name: String): PubSubPublisherTopic
}

external class PubSubPublisherTopic {
    fun publish(message: Buffer)
}