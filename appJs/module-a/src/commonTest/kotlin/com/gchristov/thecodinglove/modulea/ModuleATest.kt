package com.gchristov.thecodinglove.modulea

import com.gchristov.thecodinglove.kmpmoduleb.KmpModuleB
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleATest {
    @Test
    fun testGreet() {
        assertEquals(
            expected = "ModuleAFunction3 + KmpModuleB(value=123)",
            actual = ModuleA(moduleB = KmpModuleB(value = 123)).name()
        )
    }
}