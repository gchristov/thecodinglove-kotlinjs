package com.gchristov.thecodinglove.search.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.search.adapter.http.*
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import com.gchristov.thecodinglove.search.domain.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.search.domain.usecase.SearchStatisticsUseCase
import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SearchAdapterModule : DiModule() {
    override fun name() = "search-adapter"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSearchApi(client = instance()) }
            bindSingleton {
                provideSearchRepository(
                    api = instance(),
                    parseHtmlTotalPostsUseCase = instance(),
                    parseHtmlPostsUseCase = instance(),
                    firebaseAdmin = instance(),
                    jsonSerializer = instance(),
                )
            }
            bindSingleton { provideSearchConfig() }
            bindProvider { provideParseHtmlTotalPostsUseCase() }
            bindProvider { provideParseHtmlPostsUseCase() }
            bindSingleton {
                provideSearchHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    searchUseCase = instance(),
                    pubSubPublisher = instance(),
                    searchConfig = instance(),
                )
            }
            bindSingleton {
                providePreloadSearchPubSubHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    preloadSearchResultUseCase = instance(),
                    pubSubSubDecoder = instance(),
                )
            }
            bindSingleton {
                provideSearchStatisticsHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    statisticsUseCase = instance(),
                )
            }
            bindSingleton {
                provideDeleteSearchSessionHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    searchRepository = instance(),
                )
            }
            bindSingleton {
                provideSearchSessionPostHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    searchRepository = instance(),
                )
            }
            bindSingleton {
                provideUpdateSearchSessionStateHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    searchRepository = instance(),
                )
            }
        }
    }

    private fun provideSearchApi(client: NetworkClient.Html) = SearchApi(client)

    private fun provideSearchRepository(
        api: SearchApi,
        parseHtmlTotalPostsUseCase: ParseHtmlTotalPostsUseCase,
        parseHtmlPostsUseCase: ParseHtmlPostsUseCase,
        firebaseAdmin: FirebaseAdmin,
        jsonSerializer: JsonSerializer.ExplicitNulls,
    ): SearchRepository = RealSearchRepository(
        apiService = api,
        parseHtmlTotalPostsUseCase = parseHtmlTotalPostsUseCase,
        parseHtmlPostsUseCase = parseHtmlPostsUseCase,
        firebaseAdmin = firebaseAdmin,
        jsonSerializer = jsonSerializer,
    )

    private fun provideSearchConfig(): SearchConfig = SearchConfig(
        postsPerPage = 4,
        preloadPubSubTopic = BuildConfig.SEARCH_PRELOAD_PUBSUB_TOPIC,
    )

    private fun provideSearchHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        searchUseCase: SearchUseCase,
        pubSubPublisher: PubSubPublisher,
        searchConfig: SearchConfig,
    ): SearchHttpHandler = SearchHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        searchUseCase = searchUseCase,
        pubSubPublisher = pubSubPublisher,
        searchConfig = searchConfig,
    )

    private fun providePreloadSearchPubSubHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        preloadSearchResultUseCase: PreloadSearchResultUseCase,
        pubSubSubDecoder: PubSubDecoder,
    ): PreloadSearchPubSubHandler = PreloadSearchPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        preloadSearchResultUseCase = preloadSearchResultUseCase,
        pubSubDecoder = pubSubSubDecoder,
    )

    private fun provideSearchStatisticsHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        statisticsUseCase: SearchStatisticsUseCase,
    ): SearchStatisticsHttpHandler = SearchStatisticsHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        statisticsUseCase = statisticsUseCase,
    )

    private fun provideDeleteSearchSessionHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        searchRepository: SearchRepository,
    ): DeleteSearchSessionHttpHandler = DeleteSearchSessionHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        searchRepository = searchRepository,
    )

    private fun provideSearchSessionPostHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        searchRepository: SearchRepository,
    ): SearchSessionPostHttpHandler = SearchSessionPostHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        searchRepository = searchRepository,
    )

    private fun provideUpdateSearchSessionStateHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        searchRepository: SearchRepository,
    ): UpdateSearchSessionStateHttpHandler = UpdateSearchSessionStateHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        searchRepository = searchRepository,
    )
}

expect fun provideParseHtmlTotalPostsUseCase(): ParseHtmlTotalPostsUseCase

expect fun provideParseHtmlPostsUseCase(): ParseHtmlPostsUseCase