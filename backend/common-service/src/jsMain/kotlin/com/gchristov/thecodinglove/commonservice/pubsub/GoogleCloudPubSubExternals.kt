package com.gchristov.thecodinglove.commonservice.pubsub

import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import kotlin.js.Promise

@JsModule("@google-cloud/pubsub")
@JsNonModule
internal external object GoogleCloudPubSubExternals {

    class PubSub {
        fun topic(name: String): Topic
    }

    class Topic {
        fun exists(): Promise<Array<Boolean>>

        fun create(): Promise<Topic>

        fun subscription(name: String): Subscription

        fun publish(message: Buffer): Promise<String>
    }

    class Subscription {
        fun exists(): Promise<Array<Boolean>>

        fun create(options: kotlin.js.Json?): Promise<Subscription>
    }
}