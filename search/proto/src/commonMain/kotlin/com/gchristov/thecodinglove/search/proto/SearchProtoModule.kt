package com.gchristov.thecodinglove.search.proto

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.search.proto.http.RealSearchApiRepository
import com.gchristov.thecodinglove.search.proto.http.SearchApi
import com.gchristov.thecodinglove.search.proto.http.SearchApiRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class SearchProtoModule(private val apiUrl: String) : DiModule() {
    override fun name() = "search-proto"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSearchApi(
                    networkClient = instance(),
                    apiUrl = apiUrl,
                )
            }
            bindSingleton {
                provideSearchApiRepository(searchApi = instance())
            }
        }
    }

    private fun provideSearchApi(
        networkClient: NetworkClient.Json,
        apiUrl: String,
    ): SearchApi = SearchApi(
        client = networkClient,
        apiUrl = apiUrl,
    )

    private fun provideSearchApiRepository(searchApi: SearchApi): SearchApiRepository =
        RealSearchApiRepository(searchApi = searchApi)
}