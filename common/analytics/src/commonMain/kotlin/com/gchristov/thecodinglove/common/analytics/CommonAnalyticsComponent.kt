package com.gchristov.thecodinglove.common.analytics

import com.gchristov.thecodinglove.common.analytics.google.AnalyticsGoogleApi
import com.gchristov.thecodinglove.common.analytics.google.RealAnalyticsGoogleRepository
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.common.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface CommonAnalyticsComponent {
    @Provides
    @Singleton
    fun provideAnalytics(networkClient: NetworkClient.Json): Analytics = RealAnalytics(
        dispatcher = Dispatchers.Default,
        analyticsGoogleRepository = RealAnalyticsGoogleRepository(
            analyticsGoogleApi = AnalyticsGoogleApi(
                client = networkClient,
                measurementId = BuildConfig.GOOGLE_ANALYTICS_MEASUREMENT_ID,
                apiSecret = BuildConfig.GOOGLE_ANALYTICS_API_SECRET,
            ),
        ),
    )
}
