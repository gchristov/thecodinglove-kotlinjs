package com.gchristov.thecodinglove.htmlparsedata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.htmlparsedata.HtmlPost

interface ParseHtmlPostsUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, List<HtmlPost>>

    data class Dto(val html: String)
}