package com.gchristov.thecodinglove.search.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.search.adapter.http.*
import com.gchristov.thecodinglove.search.adapter.pubsub.PreloadSearchPubSubHandler
import com.gchristov.thecodinglove.search.domain.model.Environment
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
            bindSingleton { provideTheCodingLoveApi(client = instance()) }
            bindSingleton {
                provideSearchRepository(
                    theCodingLoveApi = instance(),
                    parseHtmlTotalPostsUseCase = instance(),
                    parseHtmlPostsUseCase = instance(),
                    firebaseAdmin = instance(),
                    jsonSerializer = instance(),
                )
            }
            bindSingleton {
                provideSearchConfig(
                    environment = instance(),
                )
            }
            bindProvider { provideParseHtmlTotalPostsUseCase() }
            bindProvider { provideParseHtmlPostsUseCase() }
            bindSingleton {
                provideSearchHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    searchUseCase = instance(),
                    pubSubPublisher = instance(),
                    searchConfig = instance(),
                    analytics = instance(),
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

    private fun provideTheCodingLoveApi(client: NetworkClient.Html) = TheCodingLoveApi(client)

    private fun provideSearchRepository(
        theCodingLoveApi: TheCodingLoveApi,
        parseHtmlTotalPostsUseCase: ParseHtmlTotalPostsUseCase,
        parseHtmlPostsUseCase: ParseHtmlPostsUseCase,
        firebaseAdmin: FirebaseAdmin,
        jsonSerializer: JsonSerializer.ExplicitNulls,
    ): SearchRepository = RealSearchRepository(
        theCodingLoveApi = theCodingLoveApi,
        parseHtmlTotalPostsUseCase = parseHtmlTotalPostsUseCase,
        parseHtmlPostsUseCase = parseHtmlPostsUseCase,
        firebaseAdmin = firebaseAdmin,
        jsonSerializer = jsonSerializer,
    )

    private fun provideSearchConfig(environment: Environment): SearchConfig = SearchConfig(
        postsPerPage = 4,
        preloadPubSubTopic = environment.preloadSearchPubSubTopic,
    )

    private fun provideSearchHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        searchUseCase: SearchUseCase,
        pubSubPublisher: PubSubPublisher,
        searchConfig: SearchConfig,
        analytics: Analytics,
    ): SearchHttpHandler = SearchHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        searchUseCase = searchUseCase,
        pubSubPublisher = pubSubPublisher,
        searchConfig = searchConfig,
        analytics = analytics,
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