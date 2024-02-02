package com.gchristov.thecodinglove.common.pubsub

import com.gchristov.thecodinglove.common.kotlin.JsonSerializer

actual fun providePubSubPublisher(): PubSubPublisher = GoogleCloudPubSubPublisher(pubSub = PubSub)

actual fun providePubSubDecoder(
    jsonSerializer: JsonSerializer.Default
): PubSubDecoder = GoogleCloudPubSubDecoder(jsonSerializer)

private val PubSub = GoogleCloudPubSubExternals.PubSub()