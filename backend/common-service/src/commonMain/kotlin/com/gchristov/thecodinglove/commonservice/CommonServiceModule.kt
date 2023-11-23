package com.gchristov.thecodinglove.commonservice

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.http.HttpService
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSubscription
import com.gchristov.thecodinglove.commonkotlin.AppConfig
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
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
            bindProvider {
                providePubSubSubscription(
                    log = instance(),
                    appConfig = instance(),
                )
            }
        }
    }
}

expect fun provideHttpService(log: Logger): HttpService

expect fun providePubSubPublisher(): PubSubPublisher

expect fun providePubSubSubscription(
    log: Logger,
    appConfig: AppConfig,
): PubSubSubscription

expect fun providePubSubDecoder(jsonSerializer: JsonSerializer.Default): PubSubDecoder