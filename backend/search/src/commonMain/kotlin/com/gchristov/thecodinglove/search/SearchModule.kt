package com.gchristov.thecodinglove.search

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSubscription
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.searchdata.domain.SearchConfig
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SearchModule : DiModule() {
    override fun name() = "search"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
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
                    pubSubSubscription = instance(),
                    pubSubSubDecoder = instance(),
                    searchConfig = instance(),
                )
            }
        }
    }

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
        pubSubSubscription: PubSubSubscription,
        pubSubSubDecoder: PubSubDecoder,
        searchConfig: SearchConfig,
    ): PreloadSearchPubSubHandler = PreloadSearchPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        preloadSearchResultUseCase = preloadSearchResultUseCase,
        pubSubSubscription = pubSubSubscription,
        pubSubDecoder = pubSubSubDecoder,
        searchConfig = searchConfig,
    )
}