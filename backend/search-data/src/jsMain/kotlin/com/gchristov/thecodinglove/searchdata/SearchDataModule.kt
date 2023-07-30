package com.gchristov.thecodinglove.searchdata

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubPublisher
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.kmpcommonnetwork.HtmlClient
import com.gchristov.thecodinglove.searchdata.model.SearchConfig
import com.gchristov.thecodinglove.searchdata.usecase.*
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SearchDataModule : DiModule() {
    override fun name() = "search-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSearchApi(client = instance()) }
            bindSingleton {
                provideSearchRepository(
                    api = instance(),
                    parseHtmlTotalPostsUseCase = instance(),
                    parseHtmlPostsUseCase = instance(),
                    firebaseFirestore = instance(),
                    log = instance()
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
                provideSearchUseCase(
                    searchRepository = instance(),
                    searchWithHistoryUseCase = instance(),
                    pubSubPublisher = instance(),
                    jsonSerializer = instance(),
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

    private fun provideSearchApi(client: HtmlClient) = SearchApi(client)

    private fun provideSearchRepository(
        api: SearchApi,
        parseHtmlTotalPostsUseCase: ParseHtmlTotalPostsUseCase,
        parseHtmlPostsUseCase: ParseHtmlPostsUseCase,
        firebaseFirestore: FirebaseFirestore,
        log: Logger
    ): SearchRepository = RealSearchRepository(
        apiService = api,
        parseHtmlTotalPostsUseCase = parseHtmlTotalPostsUseCase,
        parseHtmlPostsUseCase = parseHtmlPostsUseCase,
        firebaseFirestore = firebaseFirestore,
        log = log
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

    private fun provideSearchUseCase(
        searchRepository: SearchRepository,
        searchWithHistoryUseCase: SearchWithHistoryUseCase,
        pubSubPublisher: PubSubPublisher,
        jsonSerializer: Json,
    ): SearchUseCase = RealSearchUseCase(
        dispatcher = Dispatchers.Default,
        searchRepository = searchRepository,
        searchWithHistoryUseCase = searchWithHistoryUseCase,
        pubSubPublisher = pubSubPublisher,
        jsonSerializer = jsonSerializer,
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