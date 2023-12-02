package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import com.gchristov.thecodinglove.commontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.commontest.FakeLogger
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.RealSlackVerifyRequestUseCase
import com.gchristov.thecodinglove.slackdata.usecase.SlackVerifyRequestUseCase
import com.gchristov.thecodinglove.slacktestfixtures.FakeSlackApiRequest
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class RealVerifySlackRequestUseCaseTest {
    @Test
    fun verifyWithMissingTimestampReturnsError(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(FakeSlackApiRequest(fakeTimestamp = null))
            assertEquals(
                expected = Either.Left(SlackVerifyRequestUseCase.Error.MissingTimestamp),
                actual = actualResult
            )
        }
    }

    @Test
    fun verifyWithInvalidTimestampReturnsError(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(FakeSlackApiRequest(fakeTimestamp = "timestamp"))
            assertEquals(
                expected = Either.Left(SlackVerifyRequestUseCase.Error.Other("Invalid number format: 'timestamp'")),
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
                expected = Either.Left(SlackVerifyRequestUseCase.Error.MissingSignature),
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
                expected = Either.Left(SlackVerifyRequestUseCase.Error.TooOld),
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
                expected = Either.Left(SlackVerifyRequestUseCase.Error.SignatureMismatch),
                actual = actualResult
            )
        }
    }

    @Test
    fun verifyWithInvalidBodySignatureReturnsError(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(
                FakeSlackApiRequest(
                    fakeTimestamp = TestClock.now().toEpochMilliseconds()
                        .toString(),
                    fakeSignature = "v0=afd7e1dbff43bfdd7860f4da361f593eff602833c931adc9ac02fa6b16d3c5e2",
                    fakeRawBody = "Test body 2"
                )
            )
            assertEquals(
                expected = Either.Left(SlackVerifyRequestUseCase.Error.SignatureMismatch),
                actual = actualResult
            )
        }
    }

    @Test
    fun verifySuccess(): TestResult {
        return runBlockingTest { useCase ->
            val actualResult = useCase.invoke(
                FakeSlackApiRequest(
                    fakeTimestamp = TestClock.now().toEpochMilliseconds()
                        .toString(),
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

    private fun runBlockingTest(testBlock: suspend (SlackVerifyRequestUseCase) -> Unit): TestResult =
        runTest {
            val useCase = RealSlackVerifyRequestUseCase(
                dispatcher = FakeCoroutineDispatcher,
                slackConfig = SlackConfig(
                    signingSecret = TestSigningSecret,
                    timestampValidityMinutes = TestTimestampValidityInMinutes,
                    requestVerificationEnabled = true,
                    clientId = TestClientId,
                    clientSecret = TestClientSecret,
                    interactivityPubSubTopic = TestInteractivityPubSubTopic,
                    slashCommandPubSubTopic = TestSlashCommandPubSubTopic,
                    monitoringUrl = TestMonitoringUrl,
                ),
                clock = TestClock,
                log = FakeLogger
            )
            testBlock(useCase)
        }
}

private const val TestSigningSecret = "12345678901234567890"
private const val TestTimestampValidityInMinutes = 5
private const val TestClientId = "client_1"
private const val TestClientSecret = "client_secret_1"
private val TestClock = object : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(1675611196847)
}
private const val TestInteractivityPubSubTopic = "topic_123"
private const val TestSlashCommandPubSubTopic = "topic_456"
private const val TestMonitoringUrl = "https://monitoring"