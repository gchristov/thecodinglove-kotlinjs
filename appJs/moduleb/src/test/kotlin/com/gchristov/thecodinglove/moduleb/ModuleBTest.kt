package com.gchristov.thecodinglove.moduleb

import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleBTest {
    @Test
    fun testGreet() {
        assertEquals(
            expected = "ModuleBf",
            actual = ModuleB().name()
        )
    }
}