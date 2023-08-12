package com.gchristov.thecodinglove.commonservicedata

import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.api.RealApiServiceRegister
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton

object CommonServiceDataModule : DiModule() {
    override fun name() = "common-service-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideApiServiceRegister() }
        }
    }

    private fun provideApiServiceRegister(): ApiServiceRegister = RealApiServiceRegister()
}