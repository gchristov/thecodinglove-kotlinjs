package com.gchristov.thecodinglove.commonservice

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import dev.gitlive.firebase.FirebaseOptions
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonServiceModule : DiModule() {
    override fun name() = "common-service"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { providePubSub(options = instance()) }
        }
    }

    private fun providePubSub(
        options: FirebaseOptions
    ) = PubSub(projectId = requireNotNull(options.projectId))
}