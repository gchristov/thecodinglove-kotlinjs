package com.gchristov.thecodinglove.common.analytics

import com.gchristov.thecodinglove.common.analytics.google.AnalyticsGoogleApi
import com.gchristov.thecodinglove.common.analytics.google.AnalyticsGoogleRepository
import com.gchristov.thecodinglove.common.analytics.google.RealAnalyticsGoogleRepository
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonAnalyticsModule : DiModule() {
    override fun name() = "common-analytics"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideAnalyticsGoogleApi(
                    networkClient = instance(),
                )
            }
            bindSingleton {
                provideAnalyticsGoogleRepository(
                    analyticsGoogleApi = instance(),
                )
            }
            bindSingleton {
                provideAnalytics(
                    analyticsGoogleRepository = instance(),
                )
            }
        }
    }

    private fun provideAnalyticsGoogleApi(
        networkClient: NetworkClient.Json,
    ): AnalyticsGoogleApi = AnalyticsGoogleApi(
        client = networkClient,
        measurementId = BuildConfig.GOOGLE_ANALYTICS_MEASUREMENT_ID,
        apiSecret = BuildConfig.GOOGLE_ANALYTICS_API_SECRET,
    )

    private fun provideAnalyticsGoogleRepository(
        analyticsGoogleApi: AnalyticsGoogleApi,
    ): AnalyticsGoogleRepository = RealAnalyticsGoogleRepository(
        analyticsGoogleApi = analyticsGoogleApi,
    )

    private fun provideAnalytics(
        analyticsGoogleRepository: AnalyticsGoogleRepository,
    ): Analytics = RealAnalytics(
        dispatcher = Dispatchers.Default,
        analyticsGoogleRepository = analyticsGoogleRepository,
    )
}