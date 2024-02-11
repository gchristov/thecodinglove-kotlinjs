package com.gchristov.thecodinglove.search.adapter

import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.search.adapter.http.SearchApi
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
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
}

expect fun provideParseHtmlTotalPostsUseCase(): ParseHtmlTotalPostsUseCase

expect fun provideParseHtmlPostsUseCase(): ParseHtmlPostsUseCase