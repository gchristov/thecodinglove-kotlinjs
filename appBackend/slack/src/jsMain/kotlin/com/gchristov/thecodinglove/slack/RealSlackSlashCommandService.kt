package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.slackdata.SlackSlashCommandRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class RealSlackSlashCommandService(
    private val slackSlashCommandRepository: SlackSlashCommandRepository
) : SlackSlashCommandService {
    override fun register() {
        exports.slackSlashCommand = slackSlashCommandRepository.observe { request, response ->
            request.fold(
                ifLeft = { error ->
                    error.printStackTrace()
                    slackSlashCommandRepository.sendErrorResponse(response)
                },
                ifRight = { command ->
                    println(command)
                    val searchSessionId: String? = null

                    // TODO: Do not use GlobalScope
                    GlobalScope.launch {
                        println("Performing search")
                        val search = SearchModule.injectSearchWithSessionUseCase()
                        val searchType = searchSessionId?.let {
                            SearchWithSessionUseCase.Type.WithSessionId(
                                query = command.text,
                                sessionId = it
                            )
                        } ?: SearchWithSessionUseCase.Type.NewSession(command.text)
                        search(searchType)
                            .fold(
                                ifLeft = {
                                    // TODO: Send better error responses
                                    slackSlashCommandRepository.sendErrorResponse(response)
                                },
                                ifRight = { searchResult ->
                                    // TODO: Send correct success responses
                                    slackSlashCommandRepository.sendResponse(
                                        result = searchResult,
                                        response = response
                                    )
                                    println("Preloading next result")
                                    val preload = SearchModule.injectPreloadSearchResultUseCase()
                                    preload(searchResult.searchSessionId)
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