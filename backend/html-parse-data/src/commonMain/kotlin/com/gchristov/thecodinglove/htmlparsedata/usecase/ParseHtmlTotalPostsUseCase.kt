package com.gchristov.thecodinglove.htmlparsedata.usecase

import arrow.core.Either

interface ParseHtmlTotalPostsUseCase {
    suspend operator fun invoke(html: String): Either<Throwable, Int>
}