package com.gchristov.thecodinglove.search

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SearchModule : DiModule() {
    override fun name() = "search"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSearchApiService(
                    apiServiceRegister = instance(),
                    jsonSerializer = instance(),
                    log = instance(),
                    searchUseCase = instance(),
                )
            }
            bindSingleton {
                provideSearchHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    searchUseCase = instance(),
                )
            }
            bindSingleton {
                providePubSubHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    searchUseCase = instance(),
                )
            }
            bindSingleton {
                providePreloadSearchPubSubService(
                    pubSubServiceRegister = instance(),
                    jsonSerializer = instance(),
                    log = instance(),
                    preloadSearchResultUseCase = instance()
                )
            }
        }
    }

    private fun provideSearchApiService(
        apiServiceRegister: ApiServiceRegister,
        jsonSerializer: Json,
        log: Logger,
        searchUseCase: SearchUseCase
    ): SearchApiService = SearchApiService(
        apiServiceRegister = apiServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        searchUseCase = searchUseCase
    )

    private fun provideSearchHttpHandler(
        jsonSerializer: Json,
        log: Logger,
        searchUseCase: SearchUseCase
    ): SearchHttpHandler = SearchHttpHandler(
        jsonSerializer = jsonSerializer,
        log = log,
        searchUseCase = searchUseCase,
    )

    private fun providePubSubHttpHandler(
        jsonSerializer: Json,
        log: Logger,
        searchUseCase: SearchUseCase
    ): PubSubHttpHandler = PubSubHttpHandler(
        jsonSerializer = jsonSerializer,
        log = log,
        searchUseCase = searchUseCase,
    )

    private fun providePreloadSearchPubSubService(
        pubSubServiceRegister: PubSubServiceRegister,
        jsonSerializer: Json,
        log: Logger,
        preloadSearchResultUseCase: PreloadSearchResultUseCase
    ): PreloadSearchPubSubService = PreloadSearchPubSubService(
        pubSubServiceRegister = pubSubServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        preloadSearchResultUseCase = preloadSearchResultUseCase
    )
}