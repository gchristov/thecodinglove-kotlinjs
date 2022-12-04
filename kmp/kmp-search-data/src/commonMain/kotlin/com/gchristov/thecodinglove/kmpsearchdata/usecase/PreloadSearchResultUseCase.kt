package com.gchristov.thecodinglove.kmpsearchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.kmpsearchdata.SearchException

/**
Use-case to preload the next search result for faster API responses. Implementations should:
- obtain the search session
- perform a normal load of a post based on the current session's search history
- persist the preloaded post in the search session
 */
interface PreloadSearchResultUseCase {
    suspend operator fun invoke(searchSessionId: String) : Either<SearchException, Unit>
}