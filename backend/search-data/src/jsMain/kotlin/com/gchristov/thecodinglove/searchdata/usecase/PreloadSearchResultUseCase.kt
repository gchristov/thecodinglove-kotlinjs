package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.searchdata.model.SearchError

interface PreloadSearchResultUseCase {
    suspend operator fun invoke(searchSessionId: String) : Either<SearchError, Unit>
}