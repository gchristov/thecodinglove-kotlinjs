package com.gchristov.thecodinglove.searchdata

import com.gchristov.thecodinglove.htmlparse.HtmlPostParser
import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.searchdata.model.SearchConfig
import com.gchristov.thecodinglove.searchdata.usecase.*
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SearchDataModule : DiModule() {
    override fun name() = "kmp-search-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSearchApi(client = instance()) }
            bindSingleton {
                provideSearchRepository(
                    api = instance(),
                    htmlPostParser = instance(),
                    firebaseFirestore = instance()
                )
            }
            bindSingleton { provideSearchConfig() }
            bindProvider {
                provideSearchWithHistoryUseCase(
                    searchRepository = instance(),
                    searchConfig = instance()
                )
            }
            bindProvider {
                provideSearchWithSessionUseCase(
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

    private fun provideSearchApi(client: HttpClient) = SearchApi(client)

    private fun provideSearchRepository(
        api: SearchApi,
        htmlPostParser: HtmlPostParser,
        firebaseFirestore: FirebaseFirestore
    ): SearchRepository = RealSearchRepository(
        apiService = api,
        htmlPostParser = htmlPostParser,
        firebaseFirestore = firebaseFirestore
    )

    private fun provideSearchConfig(): SearchConfig = SearchConfig(postsPerPage = 4)

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
}