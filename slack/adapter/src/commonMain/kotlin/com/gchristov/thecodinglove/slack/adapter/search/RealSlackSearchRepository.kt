package com.gchristov.thecodinglove.slack.adapter.search

import com.gchristov.thecodinglove.common.network.safeApiCall
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchResult
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchSessionPost
import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackSearchResult
import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackSearchSessionPost
import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackUpdateSearchSessionState
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import io.ktor.client.call.*

internal class RealSlackSearchRepository(private val slackSearchServiceApi: SlackSearchServiceApi) :
    SlackSearchRepository {
    override suspend fun search(query: String) = safeApiCall("Error during search") {
        slackSearchServiceApi.search(query).body<ApiSlackSearchResult>().toSearchResult()
    }

    override suspend fun shuffle(searchSessionId: String) = safeApiCall("Error during shuffle") {
        slackSearchServiceApi.shuffle(searchSessionId).body<ApiSlackSearchResult>().toSearchResult()
    }

    override suspend fun deleteSearchSession(searchSessionId: String) =
        safeApiCall("Error during delete search session") {
            slackSearchServiceApi.deleteSearchSession(searchSessionId)
            Unit
        }

    override suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SlackSearchRepository.SearchSessionStateDto,
    ) = safeApiCall("Error during update search session state") {
        slackSearchServiceApi.updateSearchSessionState(
            ApiSlackUpdateSearchSessionState(
                searchSessionId = searchSessionId,
                state = when (state) {
                    is SlackSearchRepository.SearchSessionStateDto.SelfDestruct -> ApiSlackUpdateSearchSessionState.ApiState.SelfDestruct
                    is SlackSearchRepository.SearchSessionStateDto.Sent -> ApiSlackUpdateSearchSessionState.ApiState.Sent
                },
            )
        )
        Unit
    }

    override suspend fun getSearchSessionPost(searchSessionId: String) =
        safeApiCall("Error during search session post") {
            slackSearchServiceApi.getSearchSessionPost(searchSessionId)
                .body<ApiSlackSearchSessionPost>()
                .toSearchSessionPost()
        }
}
