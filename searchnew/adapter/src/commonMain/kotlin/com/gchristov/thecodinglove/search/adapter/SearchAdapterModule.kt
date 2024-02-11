package com.gchristov.thecodinglove.search.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.search.adapter.http.PreloadSearchPubSubHandler
import com.gchristov.thecodinglove.search.adapter.http.SearchApi
import com.gchristov.thecodinglove.search.adapter.http.SearchHttpHandler
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import com.gchristov.thecodinglove.search.domain.usecase.PreloadSearchResultUseCase
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
    ): SearchHttpHandler = SearchHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        searchUseCase = searchUseCase,
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
}

expect fun provideParseHtmlTotalPostsUseCase(): ParseHtmlTotalPostsUseCase

expect fun provideParseHtmlPostsUseCase(): ParseHtmlPostsUseCase