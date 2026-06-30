package com.gchristov.thecodinglove.common.networktestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import kotlin.test.assertEquals

class FakeHttpResponse : HttpResponse {
    private var lastHeader: String? = null
    private var lastHeaderValue: String? = null
    private var lastData: String? = null
    private var lastStatus: Int? = null
    private var lastRedirectPath: String? = null
    private var lastFilePath: String? = null

    override suspend fun send(string: String): Either<Throwable, Unit> {
        lastData = string
        return Either.Right(Unit)
    }

    override suspend fun sendFile(localPath: String): Either<Throwable, Unit> {
        lastFilePath = localPath
        return Either.Right(Unit)
    }

    override suspend fun setHeader(header: String, value: String): Either<Throwable, Unit> {
        lastHeader = header
        lastHeaderValue = value
        return Either.Right(Unit)
    }

    override suspend fun redirect(path: String): Either<Throwable, Unit> {
        lastRedirectPath = path
        return Either.Right(Unit)
    }

    override suspend fun status(status: Int): Either<Throwable, Unit> {
        lastStatus = status
        return Either.Right(Unit)
    }

    fun assertRedirect(path: String) {
        assertEquals(
            expected = path,
            actual = lastRedirectPath
        )
    }

    fun assertEquals(
        header: String?,
        headerValue: String?,
        data: String?,
        status: Int?,
        filePath: String?,
    ) {
        assertEquals(expected = header, actual = lastHeader)
        assertEquals(expected = headerValue, actual = lastHeaderValue)
        assertEquals(expected = data, actual = lastData)
        assertEquals(expected = status, actual = lastStatus)
        assertEquals(expected = filePath, actual = lastFilePath)
    }
}
