package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SearchModule : DiModule() {
    override fun name() = "search"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSearchService(
                    searchWithSessionUseCase = instance(),
                    preloadSearchResultUseCase = instance()
                )
            }
        }
    }

    private fun provideSearchService(
        searchWithSessionUseCase: SearchWithSessionUseCase,
        preloadSearchResultUseCase: PreloadSearchResultUseCase
    ): SearchApiService = SearchApiService(
        searchWithSessionUseCase = searchWithSessionUseCase,
        preloadSearchResultUseCase = preloadSearchResultUseCase
    )
}