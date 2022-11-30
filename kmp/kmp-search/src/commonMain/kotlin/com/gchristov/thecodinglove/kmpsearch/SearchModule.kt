package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import com.gchristov.thecodinglove.kmpsearch.usecase.RealSearchUseCase
import com.gchristov.thecodinglove.kmpsearch.usecase.RealSearchWithSessionUseCase
import com.gchristov.thecodinglove.kmpsearchdata.SearchDataModule
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchConfig
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithSessionUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider

object SearchModule : DiModule() {
    override fun name() = "kmp-search"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider {
                provideSearchUseCase(
                    searchRepository = inject(),
                    searchConfig = inject()
                )
            }
            bindProvider {
                provideSearchWithSessionUseCase(
                    searchRepository = inject(),
                    searchUseCase = inject(),
                )
            }
        }
    }

    override fun moduleDependencies(): List<DI.Module> {
        return listOf(SearchDataModule.module)
    }

    private fun provideSearchUseCase(
        searchRepository: SearchRepository,
        searchConfig: SearchConfig
    ): SearchUseCase = RealSearchUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchConfig = searchConfig
    )

    private fun provideSearchWithSessionUseCase(
        searchRepository: SearchRepository,
        searchUseCase: SearchUseCase
    ): SearchWithSessionUseCase = RealSearchWithSessionUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchUseCase = searchUseCase
    )

    fun injectSearchWithSessionUseCase(): SearchWithSessionUseCase = inject()
}