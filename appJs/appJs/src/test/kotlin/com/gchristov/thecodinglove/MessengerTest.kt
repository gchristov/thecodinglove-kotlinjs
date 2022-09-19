package com.gchristov.thecodinglove

import kotlin.test.Test
import kotlin.test.assertEquals

class MessengerTest {
    @Test
    fun testGreet() {
        assertEquals(
            expected = "Helloo from multi-module Kotlin JS! Nested modules: ModuleA + ModuleB!",
            actual = Messenger().message()
        )
    }
}