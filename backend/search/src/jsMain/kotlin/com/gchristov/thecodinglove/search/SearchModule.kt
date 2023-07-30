package com.gchristov.thecodinglove.search

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubSubscription
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
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
                )
            }
        }
    }

    private fun provideSearchHttpHandler(
        jsonSerializer: Json,
        log: Logger,
        searchUseCase: SearchUseCase,
    ): SearchHttpHandler = SearchHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        searchUseCase = searchUseCase,
    )

    private fun providePreloadSearchPubSubHttpHandler(
        jsonSerializer: Json,
        log: Logger,
        preloadSearchResultUseCase: PreloadSearchResultUseCase,
        pubSubSubscription: PubSubSubscription,
    ): PreloadSearchPubSubHandler = PreloadSearchPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        preloadSearchResultUseCase = preloadSearchResultUseCase,
        pubSubSubscription = pubSubSubscription,
    )
}