package com.gchristov.thecodinglove.htmlparsedata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.htmlparsedata.HtmlPost

interface ParseHtmlPostsUseCase {
    suspend operator fun invoke(html: String): Either<Throwable, List<HtmlPost>>
}