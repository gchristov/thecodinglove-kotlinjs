package com.gchristov.thecodinglove.search.proto

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.search.proto.http.RealSearchServiceRepository
import com.gchristov.thecodinglove.search.proto.http.SearchServiceApi
import com.gchristov.thecodinglove.search.proto.http.SearchServiceRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class SearchProtoModule(private val apiUrl: String) : DiModule() {
    override fun name() = "search-proto"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSearchServiceApi(
                    networkClient = instance(),
                    apiUrl = apiUrl,
                )
            }
            bindSingleton {
                provideSearchServiceRepository(searchServiceApi = instance())
            }
        }
    }

    private fun provideSearchServiceApi(
        networkClient: NetworkClient.Json,
        apiUrl: String,
    ): SearchServiceApi = SearchServiceApi(
        client = networkClient,
        apiUrl = apiUrl,
    )

    private fun provideSearchServiceRepository(searchServiceApi: SearchServiceApi): SearchServiceRepository =
        RealSearchServiceRepository(searchServiceApi = searchServiceApi)
}