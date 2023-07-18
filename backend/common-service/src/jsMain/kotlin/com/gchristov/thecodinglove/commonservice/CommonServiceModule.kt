package com.gchristov.thecodinglove.commonservice

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.http.ExpressHttpService
import com.gchristov.thecodinglove.commonservice.http.HttpService
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

object CommonServiceModule : DiModule() {
    override fun name() = "common-service"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideHttpService(log = instance()) }
        }
    }

    private fun provideHttpService(log: Logger): HttpService = ExpressHttpService(log)
}