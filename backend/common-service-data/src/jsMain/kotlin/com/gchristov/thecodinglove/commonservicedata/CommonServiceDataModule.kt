package com.gchristov.thecodinglove.commonservicedata

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.api.RealApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.RealPubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.RealPubSubServiceRegister
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import dev.gitlive.firebase.FirebaseOptions
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonServiceDataModule : DiModule() {
    override fun name() = "common-service-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideApiServiceRegister() }
            bindSingleton { providePubSubServiceRegister() }
            bindSingleton {
                providePubSubSender(
                    log = instance(),
                    options = instance()
                )
            }
        }
    }

    private fun provideApiServiceRegister(): ApiServiceRegister = RealApiServiceRegister()

    private fun providePubSubServiceRegister(): PubSubServiceRegister = RealPubSubServiceRegister()

    private fun providePubSubSender(
        log: Logger,
        options: FirebaseOptions
    ): PubSubSender = RealPubSubSender(
        log = log,
        projectId = requireNotNull(options.projectId)
    )
}