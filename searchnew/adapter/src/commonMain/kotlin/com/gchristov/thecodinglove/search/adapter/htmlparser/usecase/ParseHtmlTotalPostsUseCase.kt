package com.gchristov.thecodinglove.search.adapter.htmlparser.usecase

import arrow.core.Either

interface ParseHtmlTotalPostsUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Int>

    data class Dto(val html: String)
}