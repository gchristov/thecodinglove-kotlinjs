package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import com.gchristov.thecodinglove.kmpsearchdata.SearchDataModule
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider

object SearchModule : DiModule() {
    override fun name() = "kmp-search"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideSearchUseCase(searchRepository = inject()) }
        }
    }

    override fun moduleDependencies(): List<DI.Module> {
        return listOf(SearchDataModule.module)
    }

    private fun provideSearchUseCase(
        searchRepository: SearchRepository
    ): SearchUseCase = SearchUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository
    )

    fun injectSearchUseCase(): SearchUseCase = inject()
}