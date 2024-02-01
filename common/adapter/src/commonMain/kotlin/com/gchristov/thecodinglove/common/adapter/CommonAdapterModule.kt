package com.gchristov.thecodinglove.common.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.adapter.http.HttpService
import com.gchristov.thecodinglove.common.adapter.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.adapter.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonAdapterModule : DiModule() {
    override fun name() = "common-adapter"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideHttpService(log = instance()) }
            bindSingleton { providePubSubDecoder(jsonSerializer = instance()) }
            bindSingleton { providePubSubPublisher() }
        }
    }
}

expect fun provideHttpService(log: Logger): HttpService

expect fun providePubSubPublisher(): PubSubPublisher

expect fun providePubSubDecoder(jsonSerializer: JsonSerializer.Default): PubSubDecoder