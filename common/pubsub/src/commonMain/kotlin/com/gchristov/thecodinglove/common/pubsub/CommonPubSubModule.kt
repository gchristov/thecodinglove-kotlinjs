package com.gchristov.thecodinglove.common.pubsub

import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonPubSubModule : DiModule() {
    override fun name() = "common-pubsub"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { providePubSubDecoder(jsonSerializer = instance()) }
            bindSingleton { providePubSubPublisher() }
        }
    }
}

expect fun providePubSubPublisher(): PubSubPublisher

expect fun providePubSubDecoder(jsonSerializer: JsonSerializer.Default): PubSubDecoder