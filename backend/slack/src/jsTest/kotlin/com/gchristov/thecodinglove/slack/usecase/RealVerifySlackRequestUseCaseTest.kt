package com.gchristov.thecodinglove.slack.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.VerifySlackRequestUseCase
import com.gchristov.thecodinglove.slacktestfixtures.FakeSlackApiRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RealVerifySlackRequestUseCaseTest {
    @Test
    fun verifyWithMissingTimestampReturnsError(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(FakeSlackApiRequest(fakeTimestamp = null))
            assertEquals(
                expected = Either.Left(VerifySlackRequestUseCase.Error.MissingTimestamp),
                actual = actualResult
            )
        }
    }

    @Test
    fun verifyWithInvalidTimestampReturnsError(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(FakeSlackApiRequest(fakeTimestamp = "timestamp"))
            assertEquals(
                expected = Either.Left(VerifySlackRequestUseCase.Error.Other("Invalid number format: 'timestamp'")),
                actual = actualResult
            )
        }
    }

    @Test
    fun verifyWithMissingSignatureReturnsError(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(
                FakeSlackApiRequest(
                    fakeTimestamp = "123",
                    fakeSignature = null
                )
            )
            assertEquals(
                expected = Either.Left(VerifySlackRequestUseCase.Error.MissingSignature),
                actual = actualResult
            )
        }
    }

    @Test
    fun verifyWithTooOldTimestampReturnsError(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(
                FakeSlackApiRequest(
                    fakeTimestamp = "1",
                    fakeSignature = "signature"
                )
            )
            assertEquals(
                expected = Either.Left(VerifySlackRequestUseCase.Error.TooOld),
                actual = actualResult
            )
        }
    }

    @Test
    fun verifyWithInvalidTimestampSignatureReturnsError(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(
                FakeSlackApiRequest(
                    fakeTimestamp = Clock.System.now().toEpochMilliseconds().toString(),
                    fakeSignature = "v0=afd7e1dbff43bfdd7860f4da361f593eff602833c931adc9ac02fa6b16d3c5e2",
                    fakeRawBody = "Test body"
                )
            )
            assertEquals(
                expected = Either.Left(VerifySlackRequestUseCase.Error.SignatureMismatch),
                actual = actualResult
            )
        }
    }

    @Test
    fun verifyWithInvalidBodySignatureReturnsError(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(
                FakeSlackApiRequest(
                    fakeTimestamp = TestClock.now().toEpochMilliseconds().toString(),
                    fakeSignature = "v0=afd7e1dbff43bfdd7860f4da361f593eff602833c931adc9ac02fa6b16d3c5e2",
                    fakeRawBody = "Test body 2"
                )
            )
            assertEquals(
                expected = Either.Left(VerifySlackRequestUseCase.Error.SignatureMismatch),
                actual = actualResult
            )
        }
    }

    @Test
    fun verifySuccess(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(
                FakeSlackApiRequest(
                    fakeTimestamp = TestClock.now().toEpochMilliseconds().toString(),
                    fakeSignature = "v0=afd7e1dbff43bfdd7860f4da361f593eff602833c931adc9ac02fa6b16d3c5e2",
                    fakeRawBody = "Test body"
                )
            )
            assertEquals(
                expected = Either.Right(Unit),
                actual = actualResult
            )
        }
    }

    private fun runBlockingTest(testBlock: suspend (VerifySlackRequestUseCase) -> Unit): TestResult =
        runTest {
            val useCase = RealVerifySlackRequestUseCase(
                dispatcher = FakeCoroutineDispatcher,
                slackConfig = SlackConfig(
                    signingSecret = TestSigningSecret,
                    timestampValidityMinutes = TestTimestampValidityInMinutes,
                    requestVerificationEnabled = true
                ),
                clock = TestClock
            )
            testBlock(useCase)
        }
}

private const val TestSigningSecret = "12345678901234567890"
private const val TestTimestampValidityInMinutes = 5
private val TestClock = object : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(1675611196847)
}
