package com.gchristov.thecodinglove.search.adapter.htmlparser.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.search.adapter.htmlparser.model.HtmlPost

interface ParseHtmlPostsUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, List<HtmlPost>>

    data class Dto(val html: String)
}