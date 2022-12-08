package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctions
import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctionsResponse
import com.gchristov.thecodinglove.commonfirebase.bodyFromJson
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.toSlashCommand
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class RealSlackSlashCommandRepository(
    private val jsonParser: Json
) : SlackSlashCommandRepository {
    override fun observe(
        callback: (
            command: Either<Exception, SlackSlashCommand>,
            response: FirebaseFunctionsResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        try {
            val command = request.body
                .bodyFromJson<ApiSlackSlashCommand>(jsonParser)
                .toSlashCommand()
            callback(Either.Right(command), response)
        } catch (error: Exception) {
            callback(Either.Left(error), response)
        }
    }

    override fun sendResponse(
        result: SearchWithSessionUseCase.Result,
        response: FirebaseFunctionsResponse
    ) {
        // TODO: Needs correct mapping
        val jsonResponse = Json.encodeToString(result.toResult())
        response.send(jsonResponse)
    }

    override fun sendErrorResponse(response: FirebaseFunctionsResponse) {
        // TODO: Needs correct mapping
        val jsonResponse = Json.encodeToString(Result.Empty)
        response.send(jsonResponse)
    }
}

@Serializable
private sealed class Result {
    @Serializable
    @SerialName("empty")
    object Empty : Result()

    @Serializable
    @SerialName("valid")
    data class Valid(
        val searchSessionId: String,
        val query: String,
        val postTitle: String,
        val postUrl: String,
        val postImageUrl: String,
        val totalPosts: Int,
    ) : Result()
}

private fun SearchWithSessionUseCase.Result.toResult() = Result.Valid(
    searchSessionId = searchSessionId,
    query = query,
    postTitle = post.title,
    postUrl = post.url,
    postImageUrl = post.imageUrl,
    totalPosts = totalPosts
)