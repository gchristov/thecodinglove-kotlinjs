package com.gchristov.thecodinglove.commonservice

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.http.ExpressHttpService
import com.gchristov.thecodinglove.commonservice.pubsub.*
import com.gchristov.thecodinglove.commonservicedata.http.HttpService
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSubscription
import com.gchristov.thecodinglove.kmpcommonkotlin.AppConfig
import kotlinx.serialization.json.Json

actual fun provideHttpService(log: Logger): HttpService = ExpressHttpService(log)

actual fun providePubSubPublisher(): PubSubPublisher = GoogleCloudPubSubPublisher(pubSub = PubSub)

actual fun providePubSubSubscription(
    log: Logger,
    appConfig: AppConfig,
): PubSubSubscription = GoogleCloudPubSubSubscription(
    log = log,
    pubSub = PubSub,
    appConfig = appConfig,
)

actual fun providePubSubDecoder(jsonSerializer: Json): PubSubDecoder = GoogleCloudPubSubDecoder(jsonSerializer)

private val PubSub = GoogleCloudPubSubExternals.PubSub()