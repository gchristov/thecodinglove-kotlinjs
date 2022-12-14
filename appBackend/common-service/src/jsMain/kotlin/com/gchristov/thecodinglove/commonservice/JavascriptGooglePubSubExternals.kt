@file:JsModule("@google-cloud/pubsub")
@file:JsNonModule
package com.gchristov.thecodinglove.commonservice

import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer

external class PubSub(projectId: String) {
    fun topic(name: String): PublisherTopic
}

external class PublisherTopic {
    fun publish(message: Buffer)
}