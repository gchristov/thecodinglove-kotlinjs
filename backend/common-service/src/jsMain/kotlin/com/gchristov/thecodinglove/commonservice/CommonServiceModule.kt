package com.gchristov.thecodinglove.commonservice

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.http.ExpressHttpService
import com.gchristov.thecodinglove.commonservice.pubsub.GoogleCloudPubSubExternals
import com.gchristov.thecodinglove.commonservice.pubsub.GoogleCloudPubSubPublisher
import com.gchristov.thecodinglove.commonservice.pubsub.GoogleCloudPubSubSubscription
import com.gchristov.thecodinglove.commonservicedata.http.HttpService
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSubscription
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonServiceModule : DiModule() {
    override fun name() = "common-service"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideHttpService(log = instance()) }
            bindSingleton { provideGoogleCloudPubSub() }
            bindSingleton { providePubSubPublisher(pubSub = instance()) }
            bindProvider {
                providePubSubSubscription(
                    log = instance(),
                    pubSub = instance(),
                )
            }
        }
    }

    private fun provideHttpService(log: Logger): HttpService = ExpressHttpService(log)

    private fun provideGoogleCloudPubSub(): GoogleCloudPubSubExternals.PubSub = GoogleCloudPubSubExternals.PubSub()

    private fun providePubSubPublisher(
        pubSub: GoogleCloudPubSubExternals.PubSub
    ): PubSubPublisher = GoogleCloudPubSubPublisher(pubSub = pubSub)

    private fun providePubSubSubscription(
        log: Logger,
        pubSub: GoogleCloudPubSubExternals.PubSub,
    ): PubSubSubscription = GoogleCloudPubSubSubscription(
        log = log,
        pubSub = pubSub,
    )
}