package com.gchristov.thecodinglove.moduleb

import com.gchristov.thecodinglove.kmpmoduleb.KmpModuleB
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleBTest {
    @Test
    fun testGreet() {
        assertEquals(
            expected = "KmpModuleB123",
            actual = KmpModuleB(value = 123).name()
        )
    }
}