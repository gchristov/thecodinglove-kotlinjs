package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest

interface VerifySlackRequestUseCase {
    suspend operator fun invoke(request: ApiRequest): Either<Throwable, Unit>

    sealed class Error(message: String? = null) : Throwable(message) {
        object MissingTimestamp : Error()
        object MissingSignature : Error()
        object TooOld : Error()
        object SignatureMismatch : Error()
        data class Other(override val message: String?) : Error(message)
    }
}