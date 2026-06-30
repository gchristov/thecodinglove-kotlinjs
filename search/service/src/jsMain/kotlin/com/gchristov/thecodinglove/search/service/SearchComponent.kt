package com.gchristov.thecodinglove.search.service

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.CommonAnalyticsComponent
import com.gchristov.thecodinglove.common.firebase.CommonFirebaseComponent
import com.gchristov.thecodinglove.common.firebase.firestore.FirestoreMigration
import com.gchristov.thecodinglove.common.kotlin.di.CommonKotlinComponent
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.common.monitoring.CommonMonitoringComponent
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.CommonNetworkComponent
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.pubsub.CommonPubSubComponent
import com.gchristov.thecodinglove.common.slack.CommonSlackComponent
import com.gchristov.thecodinglove.search.adapter.SearchAdapterComponent
import com.gchristov.thecodinglove.search.adapter.http.*
import com.gchristov.thecodinglove.search.adapter.pubsub.SearchPreloadPubSubHandler
import com.gchristov.thecodinglove.search.domain.SearchDomainComponent
import com.gchristov.thecodinglove.search.domain.model.Environment
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Singleton
@Component
abstract class SearchComponent(
    @get:Provides val environment: Environment,
) : CommonKotlinComponent,
    CommonNetworkComponent,
    CommonSlackComponent,
    CommonMonitoringComponent,
    CommonAnalyticsComponent,
    CommonFirebaseComponent,
    CommonPubSubComponent,
    SearchDomainComponent,
    SearchAdapterComponent {

    abstract val searchHttpHandler: SearchHttpHandler
    abstract val searchPreloadPubSubHandler: SearchPreloadPubSubHandler
    abstract val searchStatisticsHttpHandler: SearchStatisticsHttpHandler
    abstract val deleteSearchSessionHttpHandler: DeleteSearchSessionHttpHandler
    abstract val searchSessionPostHttpHandler: SearchSessionPostHttpHandler
    abstract val updateSearchSessionStateHttpHandler: UpdateSearchSessionStateHttpHandler
    abstract val httpService: HttpService
    abstract val monitoringLogWriter: MonitoringLogWriter
    abstract val log: Logger
    abstract val firestoreMigrations: List<FirestoreMigration>
}
