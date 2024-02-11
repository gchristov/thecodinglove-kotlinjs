package com.gchristov.thecodinglove.search.domain

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import com.gchristov.thecodinglove.search.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

object SearchDomainModule : DiModule() {
    override fun name() = "search-domain"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider {
                provideSearchWithHistoryUseCase(
                    searchRepository = instance(),
                    searchConfig = instance()
                )
            }
            bindProvider {
                provideSearchUseCase(
                    searchRepository = instance(),
                    searchWithHistoryUseCase = instance(),
                )
            }
            bindProvider {
                providePreloadSearchResultUseCase(
                    searchRepository = instance(),
                    searchWithHistoryUseCase = instance(),
                )
            }
        }
    }

    private fun provideSearchWithHistoryUseCase(
        searchRepository: SearchRepository,
        searchConfig: SearchConfig,
    ): SearchWithHistoryUseCase = RealSearchWithHistoryUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchConfig = searchConfig
    )

    private fun provideSearchUseCase(
        searchRepository: SearchRepository,
        searchWithHistoryUseCase: SearchWithHistoryUseCase,
    ): SearchUseCase = RealSearchUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchWithHistoryUseCase = searchWithHistoryUseCase,
    )

    private fun providePreloadSearchResultUseCase(
        searchRepository: SearchRepository,
        searchWithHistoryUseCase: SearchWithHistoryUseCase
    ): PreloadSearchResultUseCase = RealPreloadSearchResultUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchWithHistoryUseCase = searchWithHistoryUseCase
    )
}