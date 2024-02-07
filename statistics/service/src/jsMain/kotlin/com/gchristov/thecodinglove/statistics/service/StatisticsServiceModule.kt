package com.gchristov.thecodinglove.statistics.service

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.statistics.domain.model.Environment
import org.kodein.di.DI
import org.kodein.di.bindSingleton

class StatisticsServiceModule(private val environment: Environment) : DiModule() {
    override fun name() = "statistics-service"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { environment }
        }
    }
}