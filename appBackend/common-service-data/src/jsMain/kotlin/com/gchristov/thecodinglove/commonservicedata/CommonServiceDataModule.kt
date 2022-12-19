package com.gchristov.thecodinglove.commonservicedata

import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.RealPubSubSender
import com.gchristov.thecodinglove.kmpcommondi.DiModule
import dev.gitlive.firebase.FirebaseOptions
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonServiceDataModule : DiModule() {
    override fun name() = "common-service-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { providePubSubSender(options = instance()) }
        }
    }

    private fun providePubSubSender(
        options: FirebaseOptions
    ): PubSubSender = RealPubSubSender(projectId = requireNotNull(options.projectId))
}