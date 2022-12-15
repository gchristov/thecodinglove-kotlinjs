package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.searchdata.SearchException

interface PreloadSearchResultUseCase {
    suspend operator fun invoke(searchSessionId: String) : Either<SearchException, Unit>
}