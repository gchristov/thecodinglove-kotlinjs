package com.gchristov.thecodinglove.commonservice.pubsub

import com.gchristov.thecodinglove.commonkotlin.Buffer
import kotlin.js.Promise

@JsModule("@google-cloud/pubsub")
@JsNonModule
internal external object GoogleCloudPubSubExternals {

    class PubSub {
        fun topic(name: String): Topic
    }

    class Topic {
        fun publish(message: Buffer): Promise<String>
    }
}