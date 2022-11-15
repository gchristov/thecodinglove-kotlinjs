package com.gchristov.thecodinglove

import kotlin.test.Test
import kotlin.test.assertEquals

class MessengerTest {
    @Test
    fun testGreet() {
        assertEquals(
            expected = "Hello from multi-module Kotlin JS! Nested modules: ModuleAFunction3 + KmpModuleB(value=123)!",
            actual = Messenger().message()
        )
    }
}