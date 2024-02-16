package com.gchristov.thecodinglove.statistics.proto

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.statistics.proto.http.RealStatisticsServiceRepository
import com.gchristov.thecodinglove.statistics.proto.http.StatisticsServiceApi
import com.gchristov.thecodinglove.statistics.proto.http.StatisticsServiceRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class StatisticsProtoModule(private val apiUrl: String) : DiModule() {
    override fun name() = "statistics-proto"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideStatisticsServiceApi(
                    networkClient = instance(),
                    apiUrl = apiUrl,
                )
            }
            bindSingleton {
                provideStatisticsServiceRepository(statisticsServiceApi = instance())
            }
        }
    }

    private fun provideStatisticsServiceApi(
        networkClient: NetworkClient.Json,
        apiUrl: String,
    ): StatisticsServiceApi = StatisticsServiceApi(
        client = networkClient,
        apiUrl = apiUrl,
    )

    private fun provideStatisticsServiceRepository(statisticsServiceApi: StatisticsServiceApi): StatisticsServiceRepository =
        RealStatisticsServiceRepository(statisticsServiceApi = statisticsServiceApi)
}