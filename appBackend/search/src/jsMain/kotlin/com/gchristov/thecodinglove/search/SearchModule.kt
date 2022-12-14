package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.commonservice.PubSub
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
                    jsonSerializer = instance(),
                    pubSub = instance(),
                    searchWithSessionUseCase = instance()
                )
            }
            bindSingleton {
                providePreloadPubSubService(
                    jsonSerializer = instance(),
                    preloadSearchResultUseCase = instance()
                )
            }
        }
    }

    private fun provideSearchApiService(
        jsonSerializer: Json,
        pubSub: PubSub,
        searchWithSessionUseCase: SearchWithSessionUseCase
    ): SearchApiService = SearchApiService(
        jsonSerializer = jsonSerializer,
        pubSub = pubSub,
        searchWithSessionUseCase = searchWithSessionUseCase
    )

    private fun providePreloadPubSubService(
        jsonSerializer: Json,
        preloadSearchResultUseCase: PreloadSearchResultUseCase
    ): PreloadPubSubService = PreloadPubSubService(
        jsonSerializer = jsonSerializer,
        preloadSearchResultUseCase = preloadSearchResultUseCase
    )
}