package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import com.gchristov.thecodinglove.search.usecase.RealPreloadSearchResultUseCase
import com.gchristov.thecodinglove.search.usecase.RealSearchWithHistoryUseCase
import com.gchristov.thecodinglove.search.usecase.RealSearchWithSessionUseCase
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.model.SearchConfig
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithHistoryUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider

object SearchModule : DiModule() {
    override fun name() = "search"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider {
                provideSearchWithHistoryUseCase(
                    searchRepository = inject(),
                    searchConfig = inject()
                )
            }
            bindProvider {
                provideSearchWithSessionUseCase(
                    searchRepository = inject(),
                    searchWithHistoryUseCase = inject(),
                )
            }
            bindProvider {
                providePreloadSearchResultUseCase(
                    searchRepository = inject(),
                    searchWithHistoryUseCase = inject(),
                )
            }
        }
    }

    override fun moduleDependencies(): List<DI.Module> {
        return listOf(SearchDataModule.module)
    }

    private fun provideSearchWithHistoryUseCase(
        searchRepository: SearchRepository,
        searchConfig: SearchConfig
    ): SearchWithHistoryUseCase = RealSearchWithHistoryUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchConfig = searchConfig
    )

    private fun provideSearchWithSessionUseCase(
        searchRepository: SearchRepository,
        searchWithHistoryUseCase: SearchWithHistoryUseCase
    ): SearchWithSessionUseCase = RealSearchWithSessionUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchWithHistoryUseCase = searchWithHistoryUseCase
    )

    private fun providePreloadSearchResultUseCase(
        searchRepository: SearchRepository,
        searchWithHistoryUseCase: SearchWithHistoryUseCase
    ): PreloadSearchResultUseCase = RealPreloadSearchResultUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchWithHistoryUseCase = searchWithHistoryUseCase
    )

    fun injectSearchWithSessionUseCase(): SearchWithSessionUseCase = inject()

    fun injectPreloadSearchResultUseCase(): PreloadSearchResultUseCase = inject()
}