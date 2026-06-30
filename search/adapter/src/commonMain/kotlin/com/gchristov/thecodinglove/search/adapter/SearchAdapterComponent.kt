package com.gchristov.thecodinglove.search.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.search.adapter.http.*
import com.gchristov.thecodinglove.search.adapter.pubsub.SearchPreloadPubSubHandler
import com.gchristov.thecodinglove.search.domain.model.Environment
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import com.gchristov.thecodinglove.search.domain.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.search.domain.usecase.SearchStatisticsUseCase
import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface SearchAdapterComponent {
    @Provides
    @Singleton
    fun provideSearchConfig(environment: Environment): SearchConfig = SearchConfig(
        postsPerPage = 5,
        searchSessionResultCreatedPubSubTopic = environment.searchSessionResultCreatedPubSubTopic,
    )

    @Provides
    @Singleton
    fun provideSearchRepository(
        networkClient: NetworkClient.Html,
        firebaseAdmin: FirebaseAdmin,
        jsonSerializer: JsonSerializer.ExplicitNulls,
    ): SearchRepository = RealSearchRepository(
        theCodingLoveApi = TheCodingLoveApi(networkClient),
        parseHtmlTotalPostsUseCase = provideParseHtmlTotalPostsUseCase(),
        parseHtmlPostsUseCase = provideParseHtmlPostsUseCase(),
        firebaseAdmin = firebaseAdmin,
        jsonSerializer = jsonSerializer,
    )

    @Provides
    @Singleton
    fun provideSearchHttpHandler(
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

    @Provides
    @Singleton
    fun provideSearchPreloadPubSubHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        preloadSearchResultUseCase: PreloadSearchResultUseCase,
        pubSubDecoder: PubSubDecoder,
    ): SearchPreloadPubSubHandler = SearchPreloadPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        pubSubDecoder = pubSubDecoder,
        preloadSearchResultUseCase = preloadSearchResultUseCase,
    )

    @Provides
    @Singleton
    fun provideSearchStatisticsHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        statisticsUseCase: SearchStatisticsUseCase,
    ): SearchStatisticsHttpHandler = SearchStatisticsHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        statisticsUseCase = statisticsUseCase,
    )

    @Provides
    @Singleton
    fun provideDeleteSearchSessionHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        searchRepository: SearchRepository,
    ): DeleteSearchSessionHttpHandler = DeleteSearchSessionHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        searchRepository = searchRepository,
    )

    @Provides
    @Singleton
    fun provideSearchSessionPostHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        searchRepository: SearchRepository,
    ): SearchSessionPostHttpHandler = SearchSessionPostHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        searchRepository = searchRepository,
    )

    @Provides
    @Singleton
    fun provideUpdateSearchSessionStateHttpHandler(
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
