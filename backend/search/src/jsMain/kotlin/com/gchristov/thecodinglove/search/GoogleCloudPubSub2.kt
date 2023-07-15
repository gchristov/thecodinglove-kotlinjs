package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import kotlin.js.Json
import kotlin.js.Promise

@JsModule("@google-cloud/pubsub")
@JsNonModule
internal external object GoogleCloudPubSub2 {

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

        fun create(options: Json?): Promise<Subscription>
    }
}