package com.gchristov.thecodinglove.common.pubsub

import com.gchristov.thecodinglove.common.kotlin.JsonSerializer

actual fun providePubSubPublisher(): PubSubPublisher = GoogleCloudPubSubPublisher(
    pubSub = PubSub,
    cloudTasks = CloudTasks,
)

actual fun providePubSubDecoder(
    jsonSerializer: JsonSerializer.Default
): PubSubDecoder = GoogleCloudPubSubDecoder(jsonSerializer)

private val PubSub = GoogleCloudPubSubExternals.PubSub()
private val CloudTasks = GoogleCloudTasksExternals.CloudTasks()