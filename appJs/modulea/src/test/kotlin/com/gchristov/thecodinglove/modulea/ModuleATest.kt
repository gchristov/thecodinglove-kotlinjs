package com.gchristov.thecodinglove.modulea

import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleATest {
    @Test
    fun testGreet() {
        assertEquals(
            expected = "ModuleA + ModuleB",
            actual = ModuleA().name()
        )
    }
}