package com.gchristov.thecodinglove.modulea

import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleATest {
    @Test
    fun testGreet() {
        assertEquals(
            expected = "ModuleAFunction3 + KmpModuleB123",
            actual = ModuleA().name()
        )
    }
}