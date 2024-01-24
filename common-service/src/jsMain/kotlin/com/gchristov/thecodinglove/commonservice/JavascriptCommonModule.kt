package com.gchristov.thecodinglove.commonservice

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonservice.http.ExpressHttpService
import com.gchristov.thecodinglove.commonservice.pubsub.GoogleCloudPubSubDecoder
import com.gchristov.thecodinglove.commonservice.pubsub.GoogleCloudPubSubExternals
import com.gchristov.thecodinglove.commonservice.pubsub.GoogleCloudPubSubPublisher
import com.gchristov.thecodinglove.commonservicedata.http.HttpService
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher

actual fun provideHttpService(log: Logger): HttpService = ExpressHttpService(log)

actual fun providePubSubPublisher(): PubSubPublisher = GoogleCloudPubSubPublisher(pubSub = PubSub)

actual fun providePubSubDecoder(
    jsonSerializer: JsonSerializer.Default
): PubSubDecoder = GoogleCloudPubSubDecoder(jsonSerializer)

private val PubSub = GoogleCloudPubSubExternals.PubSub()