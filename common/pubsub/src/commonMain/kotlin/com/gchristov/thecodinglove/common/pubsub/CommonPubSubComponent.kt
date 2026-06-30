package com.gchristov.thecodinglove.common.pubsub

import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface CommonPubSubComponent {
    @Provides
    @Singleton
    fun pubSubPublisher(): PubSubPublisher = providePubSubPublisher()

    @Provides
    @Singleton
    fun pubSubDecoder(jsonSerializer: JsonSerializer.Default): PubSubDecoder =
        providePubSubDecoder(jsonSerializer)
}
