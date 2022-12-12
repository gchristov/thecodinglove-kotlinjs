package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.commonservice.Service
import com.gchristov.thecodinglove.commonservice.exports
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase

class SearchService(
    private val searchRepository: SearchRepository,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase,
) : Service() {
    override fun register() {
        exports.search = registerApiCallback { request, response ->
            response.send("SEARCH")
        }
    }
}