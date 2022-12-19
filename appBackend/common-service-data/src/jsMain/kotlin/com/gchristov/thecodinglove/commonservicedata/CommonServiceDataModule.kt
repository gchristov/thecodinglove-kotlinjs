package com.gchristov.thecodinglove.commonservicedata

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import dev.gitlive.firebase.FirebaseOptions
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonServiceDataModule : DiModule() {
    override fun name() = "common-service-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { providePubSub(options = instance()) }
        }
    }

    private fun providePubSub(
        options: FirebaseOptions
    ) = PubSub(projectId = requireNotNull(options.projectId))
}