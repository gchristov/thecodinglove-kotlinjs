package com.gchristov.thecodinglove.common.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.adapter.http.ExpressHttpService
import com.gchristov.thecodinglove.common.adapter.http.HttpService
import com.gchristov.thecodinglove.common.adapter.pubsub.*
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer

actual fun provideHttpService(log: Logger): HttpService = ExpressHttpService(log)

actual fun providePubSubPublisher(): PubSubPublisher = GoogleCloudPubSubPublisher(pubSub = PubSub)

actual fun providePubSubDecoder(
    jsonSerializer: JsonSerializer.Default
): PubSubDecoder = GoogleCloudPubSubDecoder(jsonSerializer)

private val PubSub = GoogleCloudPubSubExternals.PubSub()