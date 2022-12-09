package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchService(
    private val searchRepository: SearchRepository,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase
) {
    fun register() {
        exports.search = searchRepository.observeSearchRequest { request, response ->
            request.fold(
                ifLeft = { error ->
                    error.printStackTrace()
                    searchRepository.sendSearchErrorResponse(response)
                },
                ifRight = { searchType ->
                    // TODO: Do not use GlobalScope
                    GlobalScope.launch {
                        println("Performing search")
                        searchWithSessionUseCase(searchType)
                            .fold(
                                ifLeft = {
                                    // TODO: Send better error responses
                                    searchRepository.sendSearchErrorResponse(response)
                                },
                                ifRight = { searchResult ->
                                    // TODO: Send correct success responses
                                    searchRepository.sendSearchResponse(
                                        result = searchResult,
                                        response = response
                                    )
                                    println("Preloading next result")
                                    preloadSearchResultUseCase(searchResult.searchSessionId)
                                        .fold(
                                            ifLeft = { it.printStackTrace() },
                                            ifRight = { println("Preload complete") }
                                        )
                                }
                            )
                    }
                }
            )
        }
    }
}