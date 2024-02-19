package com.gchristov.thecodinglove.search.service

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.search.domain.model.Environment
import org.kodein.di.DI
import org.kodein.di.bindSingleton

internal class SearchServiceModule(private val environment: Environment) : DiModule() {
    override fun name() = "search-service"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { environment }
        }
    }
}