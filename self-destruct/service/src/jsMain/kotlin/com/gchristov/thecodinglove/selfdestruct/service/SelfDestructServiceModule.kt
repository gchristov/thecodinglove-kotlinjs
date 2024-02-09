package com.gchristov.thecodinglove.selfdestruct.service

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.selfdestruct.domain.model.Environment
import org.kodein.di.DI
import org.kodein.di.bindSingleton

class SelfDestructServiceModule(private val environment: Environment) : DiModule() {
    override fun name() = "self-destruct-service"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { environment }
        }
    }
}