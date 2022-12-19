package com.gchristov.thecodinglove.slack.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest

interface VerifySlackRequestUseCase {
    suspend operator fun invoke(request: ApiRequest): Either<Throwable, Unit>
}