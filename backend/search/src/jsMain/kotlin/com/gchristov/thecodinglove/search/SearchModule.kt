package com.gchristov.thecodinglove.search

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
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
                    pubSubSender = instance(),
                    searchWithSessionUseCase = instance(),
                )
            }
            bindSingleton {
                providePreloadPubSubService(
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
        pubSubSender: PubSubSender,
        searchWithSessionUseCase: SearchWithSessionUseCase
    ): SearchApiService = SearchApiService(
        apiServiceRegister = apiServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        pubSubSender = pubSubSender,
        searchWithSessionUseCase = searchWithSessionUseCase
    )

    private fun providePreloadPubSubService(
        pubSubServiceRegister: PubSubServiceRegister,
        jsonSerializer: Json,
        log: Logger,
        preloadSearchResultUseCase: PreloadSearchResultUseCase
    ): PreloadPubSubService = PreloadPubSubService(
        pubSubServiceRegister = pubSubServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        preloadSearchResultUseCase = preloadSearchResultUseCase
    )
}