package com.gchristov.thecodinglove.search

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
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
                    pubSubSubDecoder = instance(),
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
        pubSubSubDecoder: PubSubDecoder,
    ): PreloadSearchPubSubHandler = PreloadSearchPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        preloadSearchResultUseCase = preloadSearchResultUseCase,
        pubSubDecoder = pubSubSubDecoder,
    )
}