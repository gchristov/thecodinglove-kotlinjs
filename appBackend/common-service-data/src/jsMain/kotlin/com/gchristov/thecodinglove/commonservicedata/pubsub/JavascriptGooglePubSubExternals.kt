// This is needed, otherwise we get "PubSub is not a constructor" error
@file:JsModule("@google-cloud/pubsub")
@file:JsNonModule
package com.gchristov.thecodinglove.commonservicedata.pubsub

import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer

@JsName("PubSub")
internal external class GoogleCloudPubSub(projectId: String) {
    fun topic(name: String): GoogleGloudPubSubTopic
}

internal external class GoogleGloudPubSubTopic {
    fun publish(message: Buffer)
}