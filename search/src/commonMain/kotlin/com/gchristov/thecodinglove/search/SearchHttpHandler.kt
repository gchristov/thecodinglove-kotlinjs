package com.gchristov.thecodinglove.search

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.searchdata.api.ApiSearchResult
import com.gchristov.thecodinglove.searchdata.api.toPost
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SearchHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    log: Logger,
    private val searchUseCase: SearchUseCase,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/search",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = searchUseCase(SearchUseCase.Dto(request.toSearchType()))
        .flatMap { searchResult ->
            response.sendJson(
                data = searchResult.toSearchResult(),
                jsonSerializer = jsonSerializer,
            )
        }
}

private fun HttpRequest.toSearchType(): SearchUseCase.Type {
    val searchQuery: String = query["searchQuery"] ?: "release"
    val searchSessionId: String? = query["searchSessionId"]
    return searchSessionId?.let {
        SearchUseCase.Type.WithSessionId(sessionId = it)
    } ?: SearchUseCase.Type.NewSession(searchQuery)
}

private fun SearchUseCase.Result.toSearchResult() = ApiSearchResult(
    searchSessionId = searchSessionId,
    query = query,
    post = post.toPost(),
    totalPosts = totalPosts
)