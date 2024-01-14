package com.gchristov.thecodinglove.commonservice

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import com.gchristov.thecodinglove.commonservicedata.http.HttpService
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonServiceModule : DiModule() {
    override fun name() = "common-service"

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