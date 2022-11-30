package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import com.gchristov.thecodinglove.kmpsearch.usecase.RealSearchWithHistoryUseCase
import com.gchristov.thecodinglove.kmpsearch.usecase.RealSearchWithSessionUseCase
import com.gchristov.thecodinglove.kmpsearchdata.SearchDataModule
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchConfig
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithHistoryUseCase
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithSessionUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider

object SearchModule : DiModule() {
    override fun name() = "kmp-search"

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

    fun injectSearchWithSessionUseCase(): SearchWithSessionUseCase = inject()
}