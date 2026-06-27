package com.gchristov.thecodinglove.common.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals

class ParseMainArgsTest {
    @Test
    fun emptyArgsReturnsEmptyMap() {
        val result = parseMainArgs(emptyArray())
        assertEquals(expected = emptyMap(), actual = result)
    }

    @Test
    fun singleFlagWithNoValueReturnsEmptyList() {
        val result = parseMainArgs(arrayOf("-port"))
        assertEquals(expected = mapOf("-port" to emptyList()), actual = result)
    }

    @Test
    fun singleFlagWithOneValueReturnsSingleElementList() {
        val result = parseMainArgs(arrayOf("-port", "8080"))
        assertEquals(expected = mapOf("-port" to listOf("8080")), actual = result)
    }

    @Test
    fun singleFlagWithMultipleValuesReturnsAllValues() {
        val result = parseMainArgs(arrayOf("-port", "8080", "9090"))
        assertEquals(expected = mapOf("-port" to listOf("8080", "9090")), actual = result)
    }

    @Test
    fun multipleFlagsEachWithValueAreParsedIndependently() {
        val result = parseMainArgs(arrayOf("-port", "8080", "-host", "localhost"))
        assertEquals(
            expected = mapOf(
                "-port" to listOf("8080"),
                "-host" to listOf("localhost"),
            ),
            actual = result,
        )
    }

    @Test
    fun multipleFlagsWithNoValuesReturnEmptyLists() {
        val result = parseMainArgs(arrayOf("-flag1", "-flag2"))
        assertEquals(
            expected = mapOf(
                "-flag1" to emptyList(),
                "-flag2" to emptyList(),
            ),
            actual = result,
        )
    }
}
