package com.gchristov.thecodinglove.search.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.pubsub.PubSubEventHandler
import com.gchristov.thecodinglove.search.adapter.pubsub.model.SearchSessionResultCreatedEvent
import com.gchristov.thecodinglove.search.domain.usecase.PreloadSearchResultUseCase

internal class SearchPreloadPubSubEventHandler(
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase,
) : PubSubEventHandler<SearchSessionResultCreatedEvent> {
    override suspend fun handle(event: SearchSessionResultCreatedEvent): Either<Throwable, Unit> =
        preloadSearchResultUseCase(PreloadSearchResultUseCase.Dto(searchSessionId = event.searchSessionId))
}
