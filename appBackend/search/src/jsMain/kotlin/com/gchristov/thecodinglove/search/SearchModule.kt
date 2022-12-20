package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.kmpcommondi.DiModule
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
                    pubSubSender = instance(),
                    searchWithSessionUseCase = instance()
                )
            }
            bindSingleton {
                providePreloadPubSubService(
                    pubSubServiceRegister = instance(),
                    jsonSerializer = instance(),
                    preloadSearchResultUseCase = instance()
                )
            }
        }
    }

    private fun provideSearchApiService(
        apiServiceRegister: ApiServiceRegister,
        jsonSerializer: Json,
        pubSubSender: PubSubSender,
        searchWithSessionUseCase: SearchWithSessionUseCase
    ): SearchApiService = SearchApiService(
        apiServiceRegister = apiServiceRegister,
        jsonSerializer = jsonSerializer,
        pubSubSender = pubSubSender,
        searchWithSessionUseCase = searchWithSessionUseCase
    )

    private fun providePreloadPubSubService(
        pubSubServiceRegister: PubSubServiceRegister,
        jsonSerializer: Json,
        preloadSearchResultUseCase: PreloadSearchResultUseCase
    ): PreloadPubSubService = PreloadPubSubService(
        pubSubServiceRegister = pubSubServiceRegister,
        jsonSerializer = jsonSerializer,
        preloadSearchResultUseCase = preloadSearchResultUseCase
    )
}