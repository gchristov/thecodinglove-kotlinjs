package com.gchristov.thecodinglove.commonservice

import kotlin.js.Promise

@JsModule("@google-cloud/pubsub")
@JsNonModule
external class PubSub(options: PubSubOptions) {

    fun createTopic(name: String): Promise<Array<Topic>>
    fun createSubscription(topic: Topic, name: String): Promise<Array<Subscription>>

    fun topic(name: String): Topic
    fun subscription(name: String): Subscription
}

data class PubSubOptions(
    val projectId: String? = null,
    val apiEndpoint: String? = null
)

external class Topic {
    fun publish(message: Buffer)
}

external class Message {
    val data: String

    fun ack()
}

external class Subscription {
    fun on(message: String, callback: (message: Message) -> Unit)
}

external object Buffer {
    fun from(any: Any): dynamic
}