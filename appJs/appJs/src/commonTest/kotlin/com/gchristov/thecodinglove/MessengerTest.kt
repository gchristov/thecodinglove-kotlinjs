package com.gchristov.thecodinglove

import kotlin.test.Test
import kotlin.test.assertEquals

class MessengerTest {
    @Test
    fun testGreet() {
        assertEquals(
            expected = "Hello from multi-module Kotlin JS! Nested modules: ModuleA + KmpModuleB123!",
            actual = Messenger().message()
        )
    }
}