package com.gchristov.thecodinglove.search.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import com.gchristov.thecodinglove.search.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface SearchDomainComponent {
    @Provides
    fun provideSearchWithHistoryUseCase(
        searchRepository: SearchRepository,
        searchConfig: SearchConfig,
    ): SearchWithHistoryUseCase = RealSearchWithHistoryUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchConfig = searchConfig,
    )

    @Provides
    fun provideSearchUseCase(
        searchRepository: SearchRepository,
        searchWithHistoryUseCase: SearchWithHistoryUseCase,
        log: Logger,
    ): SearchUseCase = RealSearchUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchWithHistoryUseCase = searchWithHistoryUseCase,
        log = log,
    )

    @Provides
    fun providePreloadSearchResultUseCase(
        searchRepository: SearchRepository,
        searchWithHistoryUseCase: SearchWithHistoryUseCase,
        log: Logger,
    ): PreloadSearchResultUseCase = RealPreloadSearchResultUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchWithHistoryUseCase = searchWithHistoryUseCase,
        log = log,
    )

    @Provides
    fun provideSearchStatisticsUseCase(
        log: Logger,
        searchRepository: SearchRepository,
    ): SearchStatisticsUseCase = RealSearchStatisticsUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        searchRepository = searchRepository,
    )
}
