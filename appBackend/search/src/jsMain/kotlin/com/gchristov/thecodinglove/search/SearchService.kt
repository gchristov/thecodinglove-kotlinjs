package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.commonservice.ApiResponse
import com.gchristov.thecodinglove.commonservice.Service
import com.gchristov.thecodinglove.commonservice.exports
import com.gchristov.thecodinglove.commonservice.get
import com.gchristov.thecodinglove.searchdata.api.toSearchResult
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SearchService(
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase,
) : Service() {
    override fun register() {
        exports.search = registerApiCallback { request, response ->
            try {
                val searchQuery: String = request.query["searchQuery"] ?: "release"
                val searchSessionId: String? = request.query["searchSessionId"]
                val searchType = searchSessionId?.let {
                    SearchWithSessionUseCase.Type.WithSessionId(
                        query = searchQuery,
                        sessionId = it
                    )
                } ?: SearchWithSessionUseCase.Type.NewSession(searchQuery)

                // TODO: Do not use GlobalScope
                GlobalScope.launch {
                    search(
                        searchType = searchType,
                        response = response
                    )
                }
            } catch (error: Exception) {
                error.printStackTrace()
                // TODO: Needs correct response mapping
                response.send("ERROR")
            }
        }
    }

    private suspend fun search(
        searchType: SearchWithSessionUseCase.Type,
        response: ApiResponse
    ) {
        println("Performing search")
        searchWithSessionUseCase(searchType)
            .fold(
                ifLeft = {
                    it.printStackTrace()
                    // TODO: Needs correct response mapping
                    response.send("ERROR")
                },
                ifRight = { searchResult ->
                    // TODO: Needs correct response mapping
                    response.send(Json.encodeToString(searchResult.toSearchResult()))
                    println("Search complete")
                    preload(searchResult.searchSessionId)
                }
            )
    }

    private suspend fun preload(searchSessionId: String) {
        println("Preloading next result")
        preloadSearchResultUseCase(searchSessionId)
            .fold(
                ifLeft = { it.printStackTrace() },
                ifRight = { println("Preload complete") }
            )
    }
}